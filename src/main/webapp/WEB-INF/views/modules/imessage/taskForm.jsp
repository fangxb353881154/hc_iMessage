<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发送任务管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			vailType('${hcTask.type}');


			$('#type').select2().on("change", function (e) {
				//console.log("change"+e);
				vailType(this.value);
			});

			function vailType(val){
				var $phone1 = $("#phone_1"),
						$phones3 = $("#phones_3"),
						$phoneNum = $("#phone_num"),
						$areaDiv = $("#areaDiv");

				if(val == '1'){//指定号码段
					$phoneNum.show();
					$phone1.show();
					$phones3.hide();
					$areaDiv.hide();
				}else if(val == 3){	//随机号码
					$phoneNum.show();
					$phone1.hide();
					$phones3.hide();
					$areaDiv.show();
				}else if(val == 2){	//指定号码
					$phoneNum.hide();
					$phone1.hide();
					$phones3.show();
					$areaDiv.hide();
				}

			}


			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<li><a href="${ctx}/imessage/task/">发送任务列表</a></li>
	<li class="active"><a href="${ctx}/imessage/task/form?id=${hcTask.id}">发送任务${not empty hcTask.id?'修改':'添加'}</a></li>
</ul><br/>
<form:form id="inputForm" modelAttribute="hcTask" action="${ctx}/imessage/task/save" method="post" enctype="multipart/form-data" class="form-horizontal">
	<form:hidden path="id"/>
	<sys:message content="${message}"/>
	<div class="control-group">
		<label class="control-label">任务标题：</label>
		<div class="controls">
			<form:input path="title" htmlEscape="false" maxlength="255" class="input-xlarge "/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">任务类型：</label>
		<div class="controls">
			<form:select path="type" class="input-xlarge required">
				<form:option value="" label=""/>
				<form:options items="${fns:getDictList('task_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</form:select>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
	<div class="control-group hide" id="phone_1" >
		<label class="control-label">随机号码段：</label>
		<div class="controls">
			<input id="phone" name="phone" class="input-xlarge required" type="text" value="${phone}" maxlength="11">
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
	<div class="control-group hide" id="phones_3">
		<label class="control-label">手机号码：</label>
		<div class="controls">
			<input id="uploadFile" name="file" type="file" style="width:330px"/>
			<span class="help-inline"><font color="red">*导入文件不能超过5M，仅允许导入“txt”格式文件！</font> </span>
		</div>
	</div>
	<div class="control-group hide" id="areaDiv">
            <label class="control-label">地区选择：</label>
            <div class="controls">
                <sys:treeselect id="area" name="area.id" value="${hcTask.area.id}" labelName="area.name" labelValue="${hcTask.area.name}"
                                title="地区" url="/sys/area/treeData" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
            </div>
	</div>
	<div class="control-group hide" id="phone_num">
		<label class="control-label">手机号发送数量：</label>
		<div class="controls">
			<form:input path="count" htmlEscape="false" maxlength="11" class="input-xlarge digits required"/>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">短信内容：</label>
		<div class="controls">
			<form:textarea path="content" htmlEscape="false" rows="4" maxlength="2000" class="input-xxlarge required"/>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
	<shiro:hasPermission name="imessage:task:edit">
	<div class="form-actions">
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>
	</shiro:hasPermission>
</form:form>
</body>
</html>