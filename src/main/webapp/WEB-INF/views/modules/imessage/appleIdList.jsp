<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>账号管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#btnImport").click(function(){
                $.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true},
                    bottomText:"导入文件不能超过5M，仅允许导入“txt”格式文件！"});
            });

           /* $("#btnDeleteAll").click(function(){
                return confirmx('确认要清空所有账号吗？', "${ctx}/imessage/appleId/deleteAll");
            })*/
        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
    </script>
</head>
<body>
<div id="importBox" class="hide">
    <form id="importForm" action="${ctx}/imessage/appleId/import" method="post" enctype="multipart/form-data"
          class="form-search" style="padding-left:20px;text-align:center;" onsubmit="loading('正在导入，请稍等...');"><br/>
        <input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
        <input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
    </form>
</div>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/imessage/appleId/list">账号列表</a></li>
</ul>
<form:form id="searchForm" modelAttribute="hcApple" action="${ctx}/imessage/appleId/list" method="post"
           class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>苹果账号：</label>
            <form:input path="appleId" htmlEscape="false" maxlength="128" class="input-medium"/>
        </li>
        <li><label>已使用：</label>
            <form:radiobuttons path="isUse" items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value"
                               htmlEscape="false"/>
        </li>
        <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;
            <input id="btnImport" class="btn btn-primary" type="button" value="导入"/>
            &nbsp;&nbsp;&nbsp;&nbsp;
            <input type="button" class="btn btn-warning" value="一键清空账号" id="btnDeleteAll" onclick="return confirmx('确认要清空所有账号吗？', '${ctx}/imessage/appleId/deleteAll');"/>
            &nbsp;&nbsp;
            <input type="button" class="btn btn-danger" value="一键还原账号状态" id="btnUpdateAllIsUse" onclick="return confirmx('确认将所有账号使用状态还原吗？', '${ctx}/imessage/appleId/updateAllIsUse');"/>
        </li>

        <li class="clearfix"></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>苹果账号</th>
        <th>苹果密码</th>
        <th>已使用</th>
        <th>创建者</th>
        <th>更新者</th>
        <shiro:hasPermission name="imessage:hcApple:edit">
            <th>操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="hcApple">
        <tr>
            <td>
                    ${hcApple.appleId}
            </td>
            <td>
                    ${hcApple.applePwd}
            </td>
            <td>
                    ${fns:getDictLabel(hcApple.isUse, 'yes_no', '')}
            </td>
            <td>
                    ${hcApple.createBy.id}
            </td>
            <td>
                    ${hcApple.updateBy.id}
            </td>
            <shiro:hasPermission name="imessage:hcApple:edit">
                <td>
                    <a href="${ctx}/imessage/hcApple/form?id=${hcApple.id}">修改</a>
                    <a href="${ctx}/imessage/hcApple/delete?id=${hcApple.id}"
                       onclick="return confirmx('确认要删除该账号吗？', this.href)">删除</a>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>