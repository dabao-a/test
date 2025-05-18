<template>
	<view>
		<!-- 弹出层组件 -->
		<vus-layer></vus-layer>
		<form>
			<view class="cu-form-group">
				<view class="title">名称</view>
				<input placeholder="请输入模板名称" v-model="projecttemp.name"></input>
			</view>
			
			<view class="cu-form-group">
				<view class="title">简介</view>
				<input placeholder="请输入模板简介" v-model="projecttemp.remark"></input>
			</view>
			
			<view class="cu-form-group">
				<view class="title">排序</view>
				<input placeholder="请输入模板排序" v-model="projecttemp.sort"></input>
			</view>
			
			<view class="grid col-1 padding-sm bg-white margin-top" @tap="add">
				<view class="margin-tb-sm text-center" >
					<button class="cu-btn bg-blue lg" >保存</button>
				</view>
			</view>
		</form>


	</view>
</template>

<script>
	export default {
		data() {
			return {
				projecttemp: {
				}
			}
		},
		created() {
			
		},
		methods: {
			// 新增
			add() {
				var rules = [
						{name: 'name',type: 'required',errmsg: '请输入模板名称'},
						{name: 'sort',type: 'number',errmsg: '排序请输入数字'}]
				
				var awardRes = this.validate.validate(this.projecttemp, rules)
				if (!awardRes.isOk) {
					this.vusui.alert(awardRes.errmsg)
					return false
				}
				this.vusui.load(3)
				this.http.post('/openapi/task/projecttemp/add', this.projecttemp).then((res) => {
					this.vusui.close("loading");
					if (res.code != 0) {
						this.vusui.alert(res.msg)
						return false
					} else {
						this.vusui.alert('新增成功', {
							yes: function() {
								var pages = getCurrentPages();
								var prevPage = pages[pages.length - 2];
								var data = {
									needReflush: 1
								}
								prevPage.$vm.data = data
								uni.navigateBack();
							}
						})
					}
				})
			}
		}
	}
</script>