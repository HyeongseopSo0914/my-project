package PSH.controller;

import PSH.dao.RegionDAO;
import PSH.model.Member; // 세션에 저장된 Member 객체를 가정
import util.DBUtil; // 필요하다면

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * /api/regions/* 로 요청이 들어오면 처리하는 서블릿 예시
 */
@WebServlet("/api/regions/*")
public class RegionServlet extends HttpServlet {

    private RegionDAO regionDAO = new RegionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        String pathInfo = request.getPathInfo(); // 예: /districts/1, /member, 등
        System.out.println("[GET] pathInfo = " + pathInfo);

        if (pathInfo == null || "/".equals(pathInfo)) {
            // 1) 모든 광역시/도 조회 -> /api/regions
            List<Map<String, Object>> regions = regionDAO.getAllRegions();
            String json = toJsonRegions(regions);
            response.getWriter().write(json);

        } else if (pathInfo.startsWith("/districts")) {
            // 2) 특정 광역시/도의 시/군/구 목록 -> /api/regions/districts/{regionId}
            handleDistrictsRequest(pathInfo, response);

        } else if ("/member".equals(pathInfo)) {
            // 3) 현재 로그인한 회원의 활동 지역 목록
            handleMemberDistricts(request, response);

        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown path");
        }
    }

    // 시/군/구 목록 조회
    private void handleDistrictsRequest(String pathInfo, HttpServletResponse response) throws IOException {
        // pathInfo 예: "/districts/1"
        // "/districts" 바로 뒤에 "/" + 숫자가 오는지 체크
        if ("/districts".equals(pathInfo) || "/districts/".equals(pathInfo)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Region ID is required");
            return;
        }

        try {
            String regionIdStr = pathInfo.substring("/districts/".length());
            int regionId = Integer.parseInt(regionIdStr);

            List<Map<String, Object>> districts = regionDAO.getDistrictsByRegion(regionId);

            if (districts.isEmpty()) {
                // 내용이 없으면 204 No Content 같은 것도 가능
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                return;
            }
            String json = toJsonDistricts(districts);
            response.getWriter().write(json);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid region ID");
        }
    }

    // 회원의 활동 지역 목록 조회
    private void handleMemberDistricts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute("loggedInMember"); // 세션에 저장된 회원 객체 가정

        if (member == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not logged in");
            return;
        }

        List<Map<String, Object>> memberDistricts = regionDAO.getMemberDistricts(member.getId());
        String json = toJsonMemberDistricts(memberDistricts);
        response.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	System.out.println("📌 [RegionServlet] 활동 지역 추가 요청 도착!");

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        // 4) 회원의 활동 지역 추가 -> POST /api/regions
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute("loggedInMember");

        if (member == null) {
        	System.out.println("❌ [RegionServlet] 로그인되지 않은 상태에서 요청됨!");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not logged in");
            return;
        }

        // JSON 바디를 읽어 districtId 파싱
        String jsonBody = readBody(request);
        // 간단하게 정규식으로 숫자만 뽑는 예시
        int districtId;
        try {
            districtId = Integer.parseInt(jsonBody.replaceAll("\\D", ""));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid district ID");
            return;
        }
        
        System.out.println("📌 [RegionServlet] 추가할 활동 지역 ID: " + districtId);

        boolean success = regionDAO.addMemberDistrict(member.getId(), districtId);
        String resultJson = String.format("{\"success\": %b, \"message\": \"%s\"}",
                success, success ? "활동 지역이 추가되었습니다." : "추가 실패");
        
        System.out.println("📌 [RegionServlet] 활동 지역 추가 완료!");

        response.getWriter().write(resultJson);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        // 5) 회원의 활동 지역 삭제 -> DELETE /api/regions/{districtId}
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute("loggedInMember");

        if (member == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not logged in");
            return;
        }

        String pathInfo = request.getPathInfo(); // 예: "/10"
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "District ID is required");
            return;
        }

        try {
            int districtId = Integer.parseInt(pathInfo.substring(1)); // "/10" -> "10"
            boolean success = regionDAO.removeMemberDistrict(member.getId(), districtId);

            String resultJson = String.format("{\"success\": %b, \"message\": \"%s\"}",
                    success, success ? "활동 지역이 삭제되었습니다." : "삭제 실패");
            response.getWriter().write(resultJson);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid district ID");
        }
    }

    // JSON 변환 도우미 메서드들
    private String toJsonRegions(List<Map<String, Object>> list) {
        // 예: [{ "id":1, "name":"서울특별시" }, ... ]
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> m = list.get(i);
            if (i > 0) sb.append(",");
            sb.append(String.format("{\"id\":%d,\"name\":\"%s\"}",
                    m.get("id"), m.get("name")));
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJsonDistricts(List<Map<String, Object>> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> m = list.get(i);
            if (i > 0) sb.append(",");
            sb.append(String.format("{\"id\":%d,\"name\":\"%s\"}",
                    m.get("id"), m.get("name")));
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJsonMemberDistricts(List<Map<String, Object>> list) {
        // [{ "regionId":1, "regionName":"서울특별시", "districtId":101, "districtName":"강남구" }, ...]
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> m = list.get(i);
            if (i > 0) sb.append(",");
            sb.append(String.format(
                    "{\"regionId\":%d,\"regionName\":\"%s\",\"districtId\":%d,\"districtName\":\"%s\"}",
                    m.get("regionId"), m.get("regionName"),
                    m.get("districtId"), m.get("districtName")));
        }
        sb.append("]");
        return sb.toString();
    }

    // 요청 바디를 전부 읽어오는 간단한 메서드
    private String readBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = request.getReader()) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
