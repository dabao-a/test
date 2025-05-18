<template>
	<view>
		<!-- 弹出层组件 -->
		<vus-layer></vus-layer>
		<form>
			<view class="cu-form-group">
				<view class="title">关键字</view>
				<input placeholder="请输入登录名/邮箱/手机号" v-model="projectuser.searchKey"></input>
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
				projectuser: {
					project: {
						id: ''
					}
				}
			}
		},
		created() {
			
		},
		onLoad(options) {
			this.projectuser.project.id = options.projectId
		},
		methods: {
			// 新增
			add() {
				this.vusui.load(3)
				this.http.post('/openapi/task/project/user/add', this.projectuser).then((res) => {
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