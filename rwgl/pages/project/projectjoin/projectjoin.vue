<template>
	<view>
		<!-- 弹出层组件 -->
		<vus-layer></vus-layer>
		
		<view class="cu-list menu" :class="['sm-border']">
			<view class="cu-item "   @tap="detail(pt.id)"   v-for="(pt,index) in projectList" :key="index" >
				<view class="content">
					<view >{{pt.name}}</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				projectList: []
			}
		},
		created () {
			this.listMyprojectJoin()
		},
		onShow() {
			var pages = getCurrentPages();
			//当前页面的数据
			var currPage = pages[pages.length - 1];  
			var data = currPage.$vm.data
			if(data != null){
				var needReflush = data.needReflush;
				if(needReflush == 1){
					this.listMyprojectJoin()
					var datax = {
						needReflush : 0
					}
					currPage.$vm.data=datax
				}
			} 
		},
		methods: {
			
			// 列表
			listMyprojectJoin () {
				this.http.post('/openapi/task/project/user/listJoin', {
					
				}).then((res) => {
					if(res.code != 0) return false
					this.projectList = res.rows;
				}) 
			},
			
			// 进入详情
			detail(id){
				uni.navigateTo({
				    url: '../../task/projecttask/projecttask?projectId='+id
				})
			}
		
		}
	}
</script>
