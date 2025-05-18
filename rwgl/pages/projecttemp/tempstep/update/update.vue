<template>
	<view>

		<!-- 弹出层组件 -->
		<vus-layer></vus-layer>
		<form>
			<view class="cu-form-group">
				<view class="title">名称</view>
				<input placeholder="请输入步骤名称" v-model="tempstep.name"></input>
			</view>
			
			<view class="cu-form-group">
				<view class="title">简介</view>
				<input placeholder="请输入步骤简介" v-model="tempstep.remark"></input>
			</view>
			
			<view class="cu-form-group">
				<view class="title">排序</view>
				<input placeholder="请输入步骤排序" v-model="tempstep.sort"></input>
			</view>
			
			<view class="grid col-1 padding-sm bg-white margin-top" @tap="update">
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
				tempstep: {
				}
			}
		},
		created() {
			// 加载类型
			this.initObject()
		},
		onLoad(options) {
			this.tempstep.id = options.id
		},
		methods: {
			// 加载对象
			initObject(){
				this.http.get(`/openapi/task/projecttemp/step/get/${this.tempstep.id}`).then((res) => {
					if(res.code != 0) {
						this.vusui.alert(res.msg)
						return false
					}
					this.tempstep = res.data
				})
			},
			// 修改
			update() {
				var rules = [
						{name: 'name',type: 'required',errmsg: '请输入步骤名称'},
						{name: 'sort',type: 'number',errmsg: '排序请输入数字'}]
				
				var awardRes = this.validate.validate(this.tempstep, rules)
				if (!awardRes.isOk) {
					this.vusui.alert(awardRes.errmsg)
					return false
				}
				this.vusui.load(3)
				this.http.post('/openapi/task/projecttemp/step/update', this.tempstep).then((res) => {
					this.vusui.close("loading");
					if (res.code != 0) {
						this.vusui.alert(res.msg)
						return;
					} else {
						this.vusui.alert('修改成功', {
							yes: function() {
								var pages = getCurrentPages();
								//上一个页面
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