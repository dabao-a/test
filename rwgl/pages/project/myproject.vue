<template>
	<view>
		<!-- 弹出层组件 -->
		<vus-layer></vus-layer>
		
		<view class="cu-list menu" :class="['sm-border']">
			<view class="cu-item arrow"   :class="modalName=='move-box-'+ index?'move-cur':''"  
				 @tap="pu(pt.id)"   v-for="(pt,index) in projectList" :key="index"
				 @touchstart="ListTouchStart" @touchmove="ListTouchMove" @touchend="ListTouchEnd" :data-target="'move-box-' + index">
				<view class="content">
					<view >{{pt.name}}</view>
					<view class="text-gray">{{pt.remark}}</view>
				</view>
				
				<view class="move">
					<view class="bg-orange" @tap.stop="openUpdate(pt.id)" >修改</view>
					<view class="bg-red"    @tap.stop="openDelete(pt.id)" >回收</view>
					<view class="bg-blue"    @tap.stop="openTask(pt.id)" >任务</view>
				</view>
			</view>
		</view>
		<view class="uni-fab-box">
			<uni-fab ref="fab" :pattern="pattern"  horizontal="left" vertical="bottom" direction="horizontal" @fabClick="openAdd()" />
		</view>
	</view>
</template>

<script>
	import uniFab from '@/components/uni-fab-add.vue'
	export default {
		components: {
			uniFab
		},
		data() {
			return {
				modalName: null,
				listTouchStart: 0,
				listTouchDirection: null,
				title: '新增',
				pattern: {
					color: '#7A7E83',
					backgroundColor: '#fff',
					selectedColor: '#007AFF',
					buttonColor: '#007AFF'
				},
				projectList: []
			}
		},
		created () {
			this.listMyproject()
		},
		onShow() {
			var pages = getCurrentPages();
			//当前页面的数据
			var currPage = pages[pages.length - 1];  
			var data = currPage.$vm.data
			if(data != null){
				var needReflush = data.needReflush;
				if(needReflush == 1){
					this.listMyproject()
					var datax = {
						needReflush : 0
					}
					currPage.$vm.data=datax
				}
			} 
		},
		methods: {
			// 进入新增界面
			openAdd() {
				uni.navigateTo({ 
				    url: 'add/add'
				})
			},
			
			// 列表
			listMyproject () {
				this.http.post('/openapi/task/project/list', {
					
				}).then((res) => {
					if(res.code != 0) return false
					this.projectList = res.rows;
				}) 
			},
			
			// 进入修改界面
			openUpdate(id){
				uni.navigateTo({
				    url: 'update/update?id='+id
				})
			},
			
			// 该项目的任务列表
			openTask(id){
				uni.navigateTo({
				    url: '../task/projecttask/projecttask?projectId='+id
				})
			},
			
			// 回收
			openDelete(id){
				var that = this
				this.vusui.confirm('您确定要回收当前记录吗？',{
					icon: 2
				}, function() {
					that.vusui.load(3)
					that.http.post('/openapi/task/project/updateStatus', {
						id: id,
						status: 1
					}).then((res) => {
						that.vusui.close("loading")
						if(res.code != 0) {
							that.vusui.alert(res.msg)
							return false
						} else {
							that.vusui.alert('回收成功')
							that.listMyproject();
						}
					}) 
				})
			},
			
			// 项目的用户列表
			pu(id){
				uni.navigateTo({
				    url: 'user/user?projectId='+id
				})
			},
			
			// ListTouch触摸开始
			ListTouchStart(e) {
				this.listTouchStart = e.touches[0].pageX
			},
			// ListTouch计算方向
			ListTouchMove(e) {
				this.listTouchDirection = e.touches[0].pageX - this.listTouchStart > 0 ? 'right' : 'left'
			},
			// ListTouch计算滚动
			ListTouchEnd(e) {
				if (this.listTouchDirection == 'left') {
					this.modalName = e.currentTarget.dataset.target
				} else {
					this.modalName = null
				}
				this.listTouchDirection = null
			}
		}
	}
</script>
