<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>Title</title>
    <meta name="decorator" content="default"/>
</head>
<body>
<form id="importForm" action="${ctx}/imessage/phone/importFile" method="post" enctype="multipart/form-data"
      class="form-search" style="padding-left:20px;text-align:center;" onsubmit="loading('正在导入，请稍等...');"><br/>
    <input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
    <label>归属地选择:</label>
    <sys:treeselect id="importArea" name="area.id" value="${hcRandPhone.area.id}" labelName="area.name" labelValue="${hcRandPhone.area.name}"
                    title="区域" url="/sys/area/treeData" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/> <br/><br/>

    <input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
</form>
</body>
</html>
