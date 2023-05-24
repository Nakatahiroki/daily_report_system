<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="constants.ForwardConst" %>
<%@ page import="constants.AttributeConst" %>


<c:set var="actTop" value="${ForwardConst.ACT_TOP.getValue()}" />
<c:set var="actEmp" value="${ForwardConst.ACT_EMP.getValue()}" />
<c:set var="actRep" value="${ForwardConst.ACT_REP.getValue()}" />
<c:set var="actAuth" value="${ForwardConst.ACT_AUTH.getValue()}" />

<c:set var="commIdx" value="${ForwardConst.CMD_INDEX.getValue()}" />




<!DOCTYPE html>
<html lang="ja">
    <head>
        <meta charset="UTF-8">
        <title><c:out value="日報管理システム" /></title>
        <link rel="stylesheet" href="<c:url value='/css/reset.css' />">
        <link rel="stylesheet" href="<c:url value='/css/style.css' />">
    </head>
    <body>
        <div id="wrapper">
            <div id="header">
                <div id="header_menu">
                    <h1>日報管理システム</h1>
                </div>
            </div>
            <div id="content">${param.content}</div>
            <div id="footer">by Taro KIrameki.</div>
       </div>
    </body>
</html>