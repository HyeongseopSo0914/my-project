package PSH.dao;

import PSH.model.BoardGame;
import util.DBUtil;

import java.sql.*;
import java.util.Arrays; 
import java.util.ArrayList;
import java.util.List;

public class BoardGameDAO {
    private static final String SELECT_ALL_GAMES = 
        "SELECT * FROM board_games ORDER BY name";
    private static final String INSERT_FAVORITE_GAME = 
        "INSERT INTO user_favorite_games (member_id, game_id) VALUES (?, ?)";
    private static final String SELECT_FAVORITE_GAMES = 
        "SELECT bg.* FROM board_games bg " +
        "JOIN user_favorite_games ufg ON bg.game_id = ufg.game_id " +
        "WHERE ufg.member_id = ?";
    private static final String SELECT_RECOMMENDED_GAMES = 
            "SELECT * FROM board_games WHERE name IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ORDER BY name";

    // 모든 게임 목록 조회
    public List<BoardGame> getAllGames() {
        List<BoardGame> games = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_GAMES);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                BoardGame game = new BoardGame();
                game.setGameId(rs.getInt("game_id"));
                game.setName(rs.getString("name"));
                game.setImageUrl(rs.getString("image_url"));
                game.setDescription(rs.getString("description"));
                game.setCreatedAt(rs.getString("created_at"));
                games.add(game);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }
 // 추천 게임 목록 조회 (지정된 게임 이름 목록)
    public List<BoardGame> getRecommendedGames() {
        List<BoardGame> games = new ArrayList<>();
        List<String> recommendedGameNames = Arrays.asList(
            "마헤", "카탄의 개척자", "스플렌더", "라스베가스",
            "세일럼 1692", "클루", "젝스님트", "수목원",
            "레지스탕스: 아발론", "뱅!"
        );
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_RECOMMENDED_GAMES)) {
            
            // 추천 게임 이름들을 쿼리 파라미터로 설정
            for (int i = 0; i < recommendedGameNames.size(); i++) {
                pstmt.setString(i + 1, recommendedGameNames.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardGame game = new BoardGame();
                    game.setGameId(rs.getInt("game_id"));
                    game.setName(rs.getString("name"));
                    game.setImageUrl(rs.getString("image_url"));
                    game.setDescription(rs.getString("description"));
                    game.setCreatedAt(rs.getString("created_at"));
                    games.add(game);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    // 사용자의 관심 게임 추가
    public boolean addFavoriteGame(int memberId, int gameId) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_FAVORITE_GAME)) {
            
            pstmt.setInt(1, memberId);
            pstmt.setInt(2, gameId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 사용자의 관심 게임 목록 조회
    public List<BoardGame> getFavoriteGames(int memberId) {
        List<BoardGame> games = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_FAVORITE_GAMES)) {
            
            pstmt.setInt(1, memberId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardGame game = new BoardGame();
                    game.setGameId(rs.getInt("game_id"));
                    game.setName(rs.getString("name"));
                    game.setImageUrl(rs.getString("image_url"));
                    game.setDescription(rs.getString("description"));
                    game.setCreatedAt(rs.getString("created_at"));
                    games.add(game);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }
    
    // 검색어로 보드게임 찾기
    public List<BoardGame> searchGames(String searchKeyword) {
        List<BoardGame> games = new ArrayList<>();
        String sql = "SELECT * FROM board_games WHERE name LIKE ? ORDER BY name";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
            pstmt.setString(1, "%" + searchKeyword + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BoardGame game = new BoardGame();
                    game.setGameId(rs.getInt("game_id"));
                    game.setName(rs.getString("name"));
                    game.setImageUrl(rs.getString("image_url"));
                    game.setDescription(rs.getString("description"));
                    game.setCreatedAt(rs.getString("created_at"));
                    games.add(game);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    // 여러 게임을 한번에 관심 게임으로 추가
    public boolean addFavoriteGames(int memberId, List<Integer> gameIds) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = true;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작
            
            pstmt = conn.prepareStatement(INSERT_FAVORITE_GAME);
            
            for (Integer gameId : gameIds) {
                pstmt.setInt(1, memberId);
                pstmt.setInt(2, gameId);
                pstmt.addBatch();
            }
            
            int[] results = pstmt.executeBatch();
            
            // 모든 배치 작업이 성공했는지 확인
            for (int result : results) {
                if (result <= 0) {
                    success = false;
                    break;
                }
            }
            
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            success = false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return success;
    }
    public List<Integer> getFavoriteGameIds(int memberId) {
        List<Integer> gameIds = new ArrayList<>();
        String sql = "SELECT game_id FROM user_favorite_games WHERE member_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    gameIds.add(rs.getInt("game_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gameIds;
    }
 // 관심 게임 삭제 메서드 추가
    public boolean removeFavoriteGame(int memberId, int gameId) {
        String sql = "DELETE FROM user_favorite_games WHERE member_id = ? AND game_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.setInt(2, gameId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public BoardGame getGameById(int gameId) {
        String sql = "SELECT * FROM board_games WHERE game_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gameId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BoardGame game = new BoardGame();
                    game.setGameId(rs.getInt("game_id"));
                    game.setName(rs.getString("name"));
                    game.setImageUrl(rs.getString("image_url"));
                    game.setDescription(rs.getString("description"));
                    game.setCreatedAt(rs.getString("created_at"));
                    return game;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}