//package com.bs23.tourbook.security;
//
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.security.web.access.ExceptionTranslationFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//
//
//public class CustomExceptionTranslationFilter extends ExceptionTranslationFilter {
//
//  public CustomExceptionTranslationFilter(AuthenticationEntryPoint authEntryPoint) {
//    super(authEntryPoint);
//  }
//
//  @Override
//  protected void sendStartAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, AuthenticationException reason) throws ServletException, IOException {
//    System.out.println("CustomExceptionTranslationFilter -------");
//
//    if (isAjax(request)) {
//      String jsonObject = "{\"message\":\"Access Denied\"," + "\"access-denied\":true}";
//      response.setContentType("application/json");
//      response.setCharacterEncoding("UTF-8");
//      PrintWriter out = response.getWriter();
//      out.print(jsonObject);
//      out.flush();
//      out.close();
//      return;
//    }
//
//    super.sendStartAuthentication(request, response, chain, reason);
//  }
//
//  private boolean isAjax(HttpServletRequest request) {
//    String requestedWithHeader = request.getHeader("X-Requested-With");
//    return "XMLHttpRequest".equals(requestedWithHeader);
//  }
//}
