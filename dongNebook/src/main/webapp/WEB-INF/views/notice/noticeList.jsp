<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>동서남북 | 공지사항</title>
<jsp:include page="/views/common/linkHead.jsp"/>
</head>
<body>
	<jsp:include page="/views/common/header.jsp" />

	<h1 style="font-size:30px;">공지사항 리스트</h1>
	<hr>
	<table border=1>
		<tr>
			<th><input type="checkbox" class="allCheck"></th><th>공지사항 번호</th><th>제목</th><th>내용</th><th>작성자</th><th>filename</th><th>filepath</th><th>작성일</th>
		</tr>
		<c:forEach items="${list }" var ="n">
			<tr>
				<td><input type="checkbox" name="noticeCheck" value=${n.rNum }></td><td>${n.rNum }</td><td>${n.noticeTitle }</td><td>${n.noticeContent }</td><td>${n.noticeWriter }</td><td>${n.filename }</td><td>${n.filepath }</td><td>${n.enrollDate }</td>			
			</tr>
		</c:forEach>
	</table>
	<p style="text-align:center">${pageNavi }</p>
	<button onclick="deleteNotice();" class="btn btn-danger">삭제</button>
	<script>
		$(function(){
			$(".allCheck").click(function (){
				if($(".allCheck").is(":checked") == true){
					$("input:checkbox[name=noticeCheck]").prop("checked",true);
				}
				if($(".allCheck").is(":checked") == false){
					$("input:checkbox[name=noticeCheck]").prop("checked", false);
				}
			});
			
			
		});
		function deleteNotice(){
			//몇 번째 체크박스가 체크되어 있는지 확인
			var arr = new Array();
			$("input:checkbox[name=noticeCheck]:checked").each(function(idx,item){
			    arr.push($("input:checkbox[name=noticeCheck]:checked").eq(idx).val());
			});
			for(var i=0; i<arr.length; i++){
				console.log(arr[i]);
			}
			location.href="/notice/deleteNotice.do?rNum="+arr;
		}
	</script>
</body>
</html>