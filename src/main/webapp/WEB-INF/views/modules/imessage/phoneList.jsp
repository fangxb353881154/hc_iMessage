<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>号码管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		/*$(document).ready(function() {
			$("#btnImport").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true},
					bottomText:"导入文件不能超过5M，仅允许导入“txt”格式文件！"});
			});
		});*/
		var importHtml = "";
		$(document).ready(function() {
			$("#btnImport").click(function(){
				if(!!$("#importBox") && !!$("#importBox").html()){
					importHtml = $("#importBox").html();
					$("#importBox").remove();
				}
				$.jBox(importHtml, {title:"导入数据", buttons:{"关闭":true},
					bottomText:"导入文件不能超过5M，仅允许导入“txt”格式文件！"});
			});
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
<div id="importBox" class="hide">
	<form id="importForm" action="${ctx}/imessage/phone/importFile" method="post" enctype="multipart/form-data"
		  class="form-search" style="padding-left:20px;text-align:center;" onsubmit="loading('正在导入，请稍等...');"><br/>
		<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
		<label>归属地选择:</label>
		<sys:treeselect id="importArea" name="area.id" value="${hcRandPhone.area.id}" labelName="area.name" labelValue="${hcRandPhone.area.name}"
						title="区域" url="/sys/area/treeData" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/> <br/><br/>

		<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
	</form>
</div>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/imessage/phone/list">号码列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="hcRandPhone" action="${ctx}/imessage/phone/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号：</label>
				<form:input path="phone" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>号码归属地：</label>
				<sys:treeselect id="area" name="area.id" value="${hcRandPhone.area.id}" labelName="area.name" labelValue="${hcRandPhone.area.name}"
					title="区域" url="/sys/area/treeData" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;
				<input id="btnImport" class="btn btn-primary" type="button" value="导入"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" class="btn btn-warning" value="一键清空号码" id="btnDeleteAll" onclick="return confirmx('确认要清空号码库吗？', '${ctx}/imessage/phone/deleteAll');"/>
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>手机号</th>
				<th>号码归属地</th>
				<th>使用次数</th>
				<th>使用状态</th>
				<th>创建人</th>
				<th>创建时间</th>
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
					${hcRandPhone.useNumber}
				</td>
				<td>${fns:getDictLabel(hcRandPhone.useStatus, 'phone_use_state', '')}</td>
				<td>
						${hcRandPhone.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${hcRandPhone.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>