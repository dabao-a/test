<template>
	<view>
		<!-- 弹出层组件 -->
		<vus-layer></vus-layer>
		
		<!-- 数据列表 -->
		<view class="cu-list menu" :class="['sm-border']">
			<view class="cu-item arrow"  @tap="detail(pt.id)"   v-for="(pt,index) in taskList" :key="index" >
				<view class="content">
					<view >{{pt.name}}</view>
					<view class="text-gray">{{pt.yxj}}-{{pt.user.userName}}</view>
				</view>
				<view class="action">
					<text class="icon iconfont" v-if="pt.status == 0" >进行中</text>
					<text class="icon iconfont" v-if="pt.status == 1" >正常完成</text>
					<text class="icon iconfont" v-if="pt.status == 2" >超时完成</text>
					<text class="icon iconfont" v-if="pt.status == 3" >不能完成</text>
				</view>
				
			</view>
		</view>
		
	</view>
</template>

<script>
	export default {
		data() {
			return {
				taskList: [],
				projectId: ''
			}
		},
		created () {
			
		},
		onLoad(options) {
			this.projectId = options.projectId
			this.loadTask()
		},
		methods: {
			// 加载我参与的项目
			loadTask(){
				this.http.post(`/openapi/task/task/listByProjectId/${this.projectId}`, {
					
				}).then((res) => {
					if(res.code != 0) return false
					this.taskList = res.rows;
				}) 
			},
			
			// 进入详情
			detail(id){
				uni.navigateTo({
				    url: '../detail/detail?id='+id
				})
			}
		}
	}
</script>
