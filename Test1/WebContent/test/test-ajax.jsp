<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>test ajax</title>
<script type="text/javascript" src="/scripts/jquery.js"></script>
<script type="text/javascript">
function go() {
	$.ajax({
		url: "/test/t2",
		data: {
			a: 1,
			b: "teset"
		},
		success: function(data) {
			alert(data)
			console.log(data);
		},
		error: function(e) {
			alert(e.status + " bad ajax request")
			if (e.status == 400) {
				alert("未登录")
			}
			console.log(e)
		}
	})
}
</script>
</head>
<body>
	<input type="button" value="go" onclick="go();"/> 
</body>
</html>