package PSH.controller;

import PSH.dao.MemberDAO;
import PSH.model.Member;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.File;
import java.util.UUID;

@WebServlet("/update-profile-image")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 10,  // 10MB
    maxRequestSize = 1024 * 1024 * 15 // 15MB
)
public class UpdateProfileServlet extends HttpServlet {
    private MemberDAO memberDAO = new MemberDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");

        if (loggedInMember == null) {
            response.getWriter().write("{\"success\":false,\"message\":\"로그인이 필요합니다.\"}");
            return;
        }

        try {
            Part filePart = request.getPart("profileImage");
            if (filePart == null) {
                response.getWriter().write("{\"success\":false,\"message\":\"이미지를 선택해주세요.\"}");
                return;
            }

            // 파일 형식 검증
            String contentType = filePart.getContentType();
            if (!contentType.startsWith("image/")) {
                response.getWriter().write("{\"success\":false,\"message\":\"이미지 파일만 업로드 가능합니다.\"}");
                return;
            }

            // 업로드 디렉토리 설정
            String uploadPath = getServletContext().getRealPath("/uploads/profiles");
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 고유한 파일명 생성
            String fileName = UUID.randomUUID().toString() + getFileExtension(filePart);
            String filePath = uploadPath + File.separator + fileName;

            // 파일 저장
            filePart.write(filePath);

            // DB에 이미지 URL 저장
            String imageUrl = request.getContextPath() + "/uploads/profiles/" + fileName;
            boolean updated = memberDAO.updateProfileImage(loggedInMember.getId(), imageUrl);

            if (updated) {
                // 세션의 회원 정보 업데이트
                loggedInMember.setProfileImageUrl(imageUrl);
                session.setAttribute("loggedInMember", loggedInMember);

                response.getWriter().write("{\"success\":true,\"message\":\"프로필 이미지가 업데이트되었습니다.\",\"imageUrl\":\"" + imageUrl + "\"}");
            } else {
                response.getWriter().write("{\"success\":false,\"message\":\"프로필 이미지 업데이트에 실패했습니다.\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\":false,\"message\":\"파일 업로드 중 오류가 발생했습니다.\"}");
        }
    }

    private String getFileExtension(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                String fileName = token.substring(token.indexOf("=") + 2, token.length() - 1);
                return fileName.substring(fileName.lastIndexOf("."));
            }
        }
        return "";
    }
}