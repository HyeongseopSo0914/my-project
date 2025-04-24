package PSH.dao;

import util.DBUtil;

import java.sql.*;
import java.util.*;

public class RegionDAO {

    // 1) 모든 광역시/도 조회
    public List<Map<String, Object>> getAllRegions() {
        String sql = "SELECT region_id, region_name FROM regions ORDER BY region_name";
        List<Map<String, Object>> regions = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("region_id"));
                map.put("name", rs.getString("region_name"));
                regions.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return regions;
    }

    // 2) 특정 광역시/도에 해당하는 시/군/구 조회
    public List<Map<String, Object>> getDistrictsByRegion(int regionId) {
        String sql = "SELECT district_id, district_name "
                   + "FROM districts "
                   + "WHERE region_id = ? "
                   + "ORDER BY district_name";

        List<Map<String, Object>> districts = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, regionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getInt("district_id"));
                    map.put("name", rs.getString("district_name"));
                    districts.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return districts;
    }

    // 3) 회원의 활동 지역 목록 조회
    public List<Map<String, Object>> getMemberDistricts(int memberId) {
        String sql = "SELECT r.region_id, r.region_name, d.district_id, d.district_name "
                   + "FROM member_districts md "
                   + "JOIN districts d ON md.district_id = d.district_id "
                   + "JOIN regions r ON d.region_id = r.region_id "
                   + "WHERE md.member_id = ? "
                   + "ORDER BY r.region_name, d.district_name";

        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("regionId", rs.getInt("region_id"));
                    map.put("regionName", rs.getString("region_name"));
                    map.put("districtId", rs.getInt("district_id"));
                    map.put("districtName", rs.getString("district_name"));
                    results.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // 4) 회원의 활동 지역 추가
    public boolean addMemberDistrict(int memberId, int districtId) {
        String sql = "INSERT INTO member_districts (member_id, district_id) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            pstmt.setInt(2, districtId);
            int rowCount = pstmt.executeUpdate();
            return rowCount > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5) 회원의 활동 지역 삭제
    public boolean removeMemberDistrict(int memberId, int districtId) {
        String sql = "DELETE FROM member_districts WHERE member_id = ? AND district_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            pstmt.setInt(2, districtId);
            int rowCount = pstmt.executeUpdate();
            return rowCount > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public String getRegionNameById(int regionId) {
        String sql = "SELECT region_name FROM regions WHERE region_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, regionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("region_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDistrictNameById(int districtId) {
        String sql = "SELECT district_name FROM districts WHERE district_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, districtId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("district_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
