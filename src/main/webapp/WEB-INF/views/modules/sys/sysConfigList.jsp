<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>配置管理</title>
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
		<li class="active"><a href="${ctx}/sys/config/">配置列表</a></li>
		<shiro:hasPermission name="sys:sysConfig:edit"><li><a href="${ctx}/sys/config/form">配置添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="sysConfig" action="${ctx}/sys/config/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>识别码：</label>
				<form:input path="key" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>识别码</th>
				<th>值</th>
				<th>描述</th>
				<th>更新时间</th>
				<shiro:hasPermission name="sys:sysConfig:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="sysConfig">
			<tr>
				<td><a href="${ctx}/sys/config/form?id=${sysConfig.id}">
					${sysConfig.key}
				</a></td>
				<td>
					${sysConfig.value}
				</td>
				<td>
					${sysConfig.depict}
				</td>
				<td>
					<fmt:formatDate value="${sysConfig.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sys:sysConfig:edit"><td>
    				<a href="${ctx}/sys/config/form?id=${sysConfig.id}">修改</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>