package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.crimes.model.Arco;
import it.polito.tdp.crimes.model.Event;


public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	public List<String> offences(){
		String sql = "SELECT distinct offense_category_id as of FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<String> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(res.getString("of"));
				} catch (Throwable t) {
					t.printStackTrace();
					
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	public List<LocalDate> date(){
		String sql = "SELECT distinct date(reported_date) as d FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<LocalDate> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(res.getTimestamp("d").toLocalDateTime().toLocalDate());
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	public void vertici(String category, LocalDate date, HashSet<String> idSet){
		String sql = "SELECT offense_type_id AS of "
				+ "FROM events "
				+ "WHERE offense_category_id=? AND date(reported_date)=?" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			
			st.setString(1, category);
			st.setDate(2, Date.valueOf(date));//DATE!!------------------------------------------------------------------------------------
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					idSet.add(res.getString("of"));
				} catch (Throwable t) {
					t.printStackTrace();
					
				}
			}
			
			conn.close();
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
	public void archi(String category, LocalDate date, LinkedList<Arco> archi){
		String sql = "SELECT e1.offense_type_id AS of1,e2.offense_type_id AS of2 , COUNT(DISTINCT e1.precinct_id) AS peso "
				+ "FROM events AS e1, events AS e2 "
				+ "WHERE e1.offense_category_id=?  AND DATE(e1.reported_date)=? "
				+ "AND e2.offense_category_id= e1.offense_category_id "
				+ "AND e1.offense_type_id> e2.offense_type_id "
				+ "AND e1.precinct_id=e2.precinct_id "
				+ "GROUP BY e1.offense_type_id, e2.offense_type_id having COUNT(DISTINCT e1.precinct_id)>0 "
				+ "";
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			
			st.setString(1, category);
			st.setDate(2, Date.valueOf(date));//DATE!!------------------------------------------------------------------------------------
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					archi.add(new Arco(res.getString("of1"),res.getString("of2"),res.getInt("peso")));
				} catch (Throwable t) {
					t.printStackTrace();
					
				}
			}
			
			conn.close();
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
}
