<template>
	<view>

		<!-- 弹出层组件 -->
		<vus-layer></vus-layer>
		
		<form>
			<view class="cu-form-group">
				<view class="title">名称</view>
				<input placeholder="请输入标签名称" v-model="label.name"></input>
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
				label: {
					id:'',
					name: ''
				}
			}
		},
		onLoad(options) {
			this.label.id = options.id
			this.label.name = options.name
		},
		methods: {
			update() {
				this.vusui.load(3)
				this.http.post('/openapi/task/label/update', this.label).then((res) => {
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