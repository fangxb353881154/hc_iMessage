<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>授权码管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/authorKey/">授权码列表</a></li>
		<shiro:hasPermission name="sys:authorKey:edit"><li><a href="${ctx}/sys/authorKey/form">授权码添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="authorKey" action="${ctx}/sys/authorKey/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>授权码：</label>
				<form:input path="creditCode" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>是否已用：</label>
				<form:radiobuttons path="isUse" items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li style="float: right;">已使用:<span style="color: mediumvioletred;">${cMap.isHas}</span>&nbsp;&nbsp;&nbsp;&nbsp;未使用：${cMap.isNot}</li>

		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>授权码</th>
				<th>被使用机器码</th>
				<th>是否已用</th>
				<th>所属用户</th>
				<th>最后使用时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="authorKey">
			<tr>
				<td>
					${authorKey.creditCode}
				</td>
				<td>
					${authorKey.machineCode}
				</td>
				<td>
					${fns:getDictLabel(authorKey.isUse, 'yes_no', '')}
				</td>
				<td>
					${authorKey.user.name}
				</td>
				<td>
					<fmt:formatDate value="${authorKey.laseDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>