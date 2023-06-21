<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="constants.ForwardConst" %>

<c:set var="actRep" value="${ForwardConst.ACT_REP.getValue()}" />
<c:set var="commShow" value="${ForwardConst.CMD_SHOW.getValue()}" />
<c:set var="commNew" value="${ForwardConst.CMD_NEW.getValue()}" />
<c:set var="commIdx" value="${ForwardConst.CMD_INDEX.getValue()}" />

<c:import url="/WEB-INF/views/layout/app.jsp">
    <c:param name="content">
        <c:if test="${flush != null}">
            <div id="flush_success">
                <c:out value="${flush}"></c:out>
            </div>
        </c:if>

        <h2>いいね！した人 一覧</h2>
        <table id="good_list">
            <tbody>
                <tr>
                    <th class="good_name">氏名</th>
                    <th class="good_date">日付</th>
                </tr>
                <c:forEach var="good" items="${goods}" varStatus="status">
                <fmt:parseDate value="${good.createdAt}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="createdAt" type="date" />
                <tr class="row${status.count % 2}">
                    <td class="good_name"><c:out value="${good.employee.name}" /></td>
                    <td class="good_date"><fmt:formatDate value='${createdAt}' pattern='yyyy-MM-dd HH:mm:ss' /></td>
                </tr>
                </c:forEach>




            </tbody>
        </table>
        <div id="pagination">
            (全 ${goods_count} 件) <br />
          <c:forEach var="i" begin="1" end="${((goods_count - 1) / maxRow) + 1}" step="1">
            <c:choose>
                <c:when test="${i == page}">
                    <c:out value="${i}" />&nbsp;
                </c:when>
                <c:otherwise>
            <a href="<c:url value='?action=${actRep}&command=goodIndex&id=${report.id}&page=${i}' />"><c:out value="${i}" /></a>&nbsp;
                </c:otherwise>
            </c:choose>
          </c:forEach>
        </div>
           <p><a href="<c:url value='?action=${actRep}&command=${commIdx}' />">一覧に戻る</a></p>

    </c:param>
</c:import>
