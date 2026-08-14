package com.hotel.controller;
import jakarta.servlet.annotation.WebServlet; import jakarta.servlet.http.*; import java.io.IOException;
@WebServlet("/logout") public class LogoutServlet extends HttpServlet { protected void doPost(HttpServletRequest q,HttpServletResponse s)throws IOException{HttpSession h=q.getSession(false);if(h!=null)h.invalidate();s.sendRedirect(q.getContextPath()+"/login");} }
