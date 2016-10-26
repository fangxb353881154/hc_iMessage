<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>号码管理</title>
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
		<li class="active"><a href="${ctx}/imessage/phone/">导入结果</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="hcRandPhoneTemp" action="${ctx}/imessage/phone/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号：</label>
				<form:input path="phone" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>是否重复：</label>
				<form:radiobuttons path="isRepeat" items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value"
								   htmlEscape="false"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;
				<a class="btn btn-primary" href="${ctx}/imessage/phone/syncTemp">确认导入</a></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>手机号</th>
				<th>号码归属地</th>
				<th>是否重复</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="hcRandPhone">
			<tr>
				<td>
					${hcRandPhone.phone}
				</td>
				<td>
					${hcRandPhone.area.name}
				</td>
				<td>
					${fns:getDictLabel(hcRandPhone.isRepeat, 'yes_no', '')}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>