<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>用户列表</title>
	<link rel="stylesheet" type="text/css" href="easyui/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="easyui/themes/icon.css">
	<link rel="stylesheet" type="text/css" href="easyui/css/demo.css">
	<script type="text/javascript" src="easyui/jquery-2.1.1.min.js"></script>
	<script type="text/javascript" src="easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="easyui/js/validateExtends.js"></script>
	<script type="text/javascript">
	function timestampToTime(timestamp) {
        var date = new Date(timestamp);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
        var Y = date.getFullYear() + '-';
        var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
        var D = date.getDate() + ' ';
        var h = date.getHours() + ':';
        var m = date.getMinutes() + ':';
        var s = date.getSeconds();
        if(s < 10){
        	s = '0' + s ;
        }
        return Y+M+D+h+m+s;
    }
	$(function() {	
		//datagrid初始化 
	    $('#dataList').datagrid({ 
	        title:'用户列表', 
	        iconCls:'icon-more',//图标 
	        border: true, 
	        collapsible:false,//是否可折叠的 
	        fit: true,//自动大小 
	        method: "post",
	        url:"UserServlet?method=getUserList&t="+new Date().getTime(),
	        idField:'id', 
	        singleSelect:false,//是否单选 
	        pagination:true,//分页控件 
	        rownumbers:true,//行号 
	        sortName:'id',
	        sortOrder:'DESC', 
	        remoteSort: false,
	        columns: [[  
				{field:'chk',checkbox: true,width:50},
 		        {field:'id',title:'ID',width:50, sortable: true},    
 		        {field:'username',title:'用户名',width:200, sortable: true},    
 		        {field:'password',title:'密码',width:200},
 		        {field:'status',title:'状态',width:100,
 		        	formatter: function(value,row,index){
							if(value==1){
								return '正常';
							}
							return '禁用';
					}	
 		        },
 		        {field:'type',title:'角色',width:150,
 		        	formatter: function(value,row,index){
						if(value==1){
							return '超管';
						}else if(value==2){
							return '普通用户';
						}else{
							return '图书管理员';
						}
				}	
 		        },
 		        {field:'createTime',title:'注册时间',width:150,
 		        	formatter: function(value,row,index){
							return timestampToTime(value); 
					}
 		        },
 		        {field:'updateTime',title:'更新时间',width:150, 
 		        	formatter: function(value,row,index){
 							return timestampToTime(value); 
 					}
				},
	 		]], 
	        toolbar: "#toolbar"
	    }); 
	    //设置分页控件 
	    var p = $('#dataList').datagrid('getPager'); 
	    $(p).pagination({ 
	        pageSize: 10,//每页显示的记录条数，默认为10 
	        pageList: [10,20,30,50,100],//可以设置每页记录条数的列表 
	        beforePageText: '第',//页数文本框前显示的汉字 
	        afterPageText: '页    共 {pages} 页', 
	        displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录', 
	    }); 
	    
	    //修改
	    $("#edit").click(function(){
	    	var selectRows = $("#dataList").datagrid("getSelections");
        	if(selectRows.length != 1){
            	$.messager.alert("消息提醒", "请选择一条数据进行操作!", "warning");
            } else{
		    	$("#editDialog").dialog("open");
            }
	    });
	  	//设置编辑学生窗口
	    $("#editDialog").dialog({
	    	title: "修改学生信息",
	    	width: 400,
	    	height: 300,
	    	iconCls: "icon-edit",
	    	modal: true,
	    	collapsible: false,
	    	minimizable: false,
	    	maximizable: false,
	    	draggable: true,
	    	closed: true,
	    	buttons: [
	    		{
					text:'提交',
					plain: true,
					iconCls:'icon-user_add',
					handler:function(){
						var validate = $("#editForm").form("validate");
						if(!validate){
							$.messager.alert("消息提醒","请检查你输入的数据!","warning");
							return;
						} else{
							$.ajax({
								type: "post",
								url: "UserServlet?method=editUserByself&t="+new Date().getTime(),
								data: $("#editForm").serialize(),
								dataType:'json',
								success: function(data){
									if(data.type == "success"){
										$.messager.alert("消息提醒","更新成功!","info");
										//关闭窗口
										$("#editDialog").dialog("close");
										//刷新表格
										$("#dataList").datagrid("reload");
									} else{
										$.messager.alert("消息提醒",data.msg,"warning");
										return;
									}
								}
							});
						}
					}
				},
			],
			onBeforeOpen: function(){
				var selectRow = $("#dataList").datagrid("getSelected");
				//设置值
				$("#edit_username").textbox('setValue', selectRow.username);
				$("#edit_password").textbox('setValue', selectRow.password);
				$("#edit_type").combobox('setValue', selectRow.type);
				$("#edit_id").val(selectRow.id);
			}
	    });
	  	$("#search-btn").click(function(){
	  		$('#dataList').datagrid('load',{
	  			username:$("#search-name").textbox('getValue')
	  		});
	  	});
	});
	</script>
</head>
<body>
	<!-- 学生列表 -->
	<table id="dataList" cellspacing="0" cellpadding="0"> 
	    
	</table> 
	<!-- 工具栏 -->
	<div id="toolbar">
		<div style="float: left;"><a id="edit" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true">修改</a></div>
			<div style="float: left;" class="datagrid-btn-separator"></div>
		<div style="margin-left: 10px;">姓名：<input id="search-name" class="easyui-textbox" /><a id="search-btn" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true">搜索</a></div>
	
	</div>
	
	
	<!-- 修改用户窗口 -->
	<div id="editDialog" style="padding: 10px"> 
    	<form id="editForm" method="post">
	    	<input type="hidden" name="id" id="edit_id">
	    	<table cellpadding="8">
				<tr>
					<td>用户名:</td>
					<td><input id="edit_username" class="easyui-textbox"
						style="width: 200px; height: 30px;" type="text" name="username"
						data-options="required:true, validType:'repeat', missingMessage:'请输入用户名'" />
					</td>
				</tr>
				<tr>
					<td>密码</td>
					<td><input id="edit_password"
						style="width: 200px; height: 30px;" class="easyui-textbox"
						type="password" name="password"
						data-options="required:true, missingMessage:'请填写密码'" /></td>
				</tr>
				<tr>
					<td>类型:</td>
					<td><select id="edit_type" class="easyui-combobox"
						data-options="editable: false, panelHeight: 50, width: 200, height: 30"
						name="type">
							<option value="2">普通用户</option>
					</select></td>
				</tr>
			</table>
	    </form>
	</div>
	
</body>
</html>