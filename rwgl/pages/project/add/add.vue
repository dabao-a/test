<template>
	<view>
		<!-- 弹出层组件 -->
		<vus-layer></vus-layer>
		<form>
			<view class="cu-form-group">
				<view class="title">名称</view>
				<input placeholder="请输入项目名称" name="projectName" v-model="project.name"></input>
			</view>
			<view class="cu-form-group">
				<view class="title">简介</view>
				<input placeholder="请输入项目简介" v-model="project.remark"></input>
			</view>
			<view class="cu-form-group">
				<view class="title">项目模板</view>
				<picker @change="pickerTemp" :range="tempList" range-key="name">
					<view class="picker">
						{{project.temp.name}}
					</view>
				</picker>
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
				project: {
					temp: {
						id: '',
						name: ''
					}
				},
				tempList: []
			}
		},
		created() {
			// 加载模板
			this.loadTempList()
		},
		methods: {
			
			// 加载模板
			loadTempList() {
				this.http.post('/openapi/task/project/tempList').then((res) => {
					if (res.code != 0) return false
					this.tempList = res.rows;
					this.project.temp = this.tempList[0]
				})
			},
			// 选择类型
			pickerTemp(e) {
				var index = e.detail.value;
				this.project.temp = this.tempList[index]
			},
			// 新增
			add() {
				this.vusui.load(3)
				this.http.post('/openapi/task/project/add', this.project).then((res) => {
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