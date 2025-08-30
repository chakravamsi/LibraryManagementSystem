package com.library.controller;

import com.library.model.Member;
import com.library.util.DBUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class MemberController {
    
    public boolean addMember(String name, String contact) {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO members (name, contact) VALUES (?, ?)");
            ps.setString(1, name);
            ps.setString(2, contact);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public ObservableList<Member> getAllMembers() {
        ObservableList<Member> members = FXCollections.observableArrayList();
        try (Connection conn = DBUtil.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM members");
            
            while (rs.next()) {
                members.add(new Member(
                    rs.getInt("member_id"),
                    rs.getString("name"),
                    rs.getString("contact")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }
}
