<template>
	<view>
		<!-- 弹出层组件 -->
		<vus-layer></vus-layer>
		
		<view class="cu-list menu" :class="['sm-border']">
			<view class="cu-item arrow"   :class="modalName=='move-box-'+ index?'move-cur':''"  
				  v-for="(pt,index) in projectuserList" :key="index"
				 @touchstart="ListTouchStart" @touchmove="ListTouchMove" @touchend="ListTouchEnd" :data-target="'move-box-' + index">
				<view class="content">
					<view >{{pt.projectName}}</view>
					<view class="text-gray">{{pt.userName}}</view>
				</view>
				
				<view class="move">
					<view class="bg-orange" @tap.stop="openUpdate(pt.id,1)" >同意</view>
					<view class="bg-red"    @tap.stop="openUpdate(pt.id,2)" >不同意</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				modalName: null,
				listTouchStart: 0,
				listTouchDirection: null,
				pattern: {
					color: '#7A7E83',
					backgroundColor: '#fff',
					selectedColor: '#007AFF',
					buttonColor: '#007AFF'
				},
				projectuserList: []
			}
		},
		created () {
			this.listTodo()
		},
		onShow() {
			var pages = getCurrentPages();
			//当前页面的数据
			var currPage = pages[pages.length - 1];  
			var data = currPage.$vm.data
			if(data != null){
				var needReflush = data.needReflush;
				if(needReflush == 1){
					this.listTodo()
					var datax = {
						needReflush : 0
					}
					currPage.$vm.data=datax
				}
			} 
		},
		methods: {
			
			// 列表
			listTodo () {
				this.http.post('/openapi/task/project/user/todoList', {
					
				}).then((res) => {
					if(res.code != 0) return false
					this.projectuserList = res.rows;
				}) 
			},
			
			// 1同意，1不同意
			openUpdate(id,status){
				var that = this
				this.vusui.confirm('您确定要如此操作吗？',{
					icon: 2
				}, function() {
					that.vusui.load(3)
					that.http.post('/openapi/task/project/user/updateStatus', {
						id: id,
						status: status
					}).then((res) => {
						that.vusui.close("loading")
						if(res.code != 0) {
							that.vusui.alert(res.msg)
							return false
						} else {
							that.vusui.alert('操作成功')
							that.listTodo();
						}
					}) 
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
