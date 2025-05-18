<template>
	<view>
		<!-- 弹出层组件 -->
		<vus-layer></vus-layer>
		
		<view class="cu-list menu" :class="['sm-border']">
			<view class="cu-item arrow"   :class="modalName=='move-box-'+ index?'move-cur':''"  
				 @tap="tempstep1(pt.id)"   v-for="(pt,index) in projecttempList" :key="index"
				 @touchstart="ListTouchStart" @touchmove="ListTouchMove" @touchend="ListTouchEnd" :data-target="'move-box-' + index">
				<view class="content">
					<view >{{pt.name}}</view>
					<view class="text-gray">{{pt.remark}}</view>
				</view>
				
				<view class="move">
					<view class="bg-orange" @tap.stop="openUpdate(pt.id)" >修改</view>
					<view class="bg-red"    @tap.stop="openDelete(pt.id)" >删除</view>
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
				projecttempList: []
			}
		},
		created () {
			this.listProjecttemp()
		},
		onShow() {
			var pages = getCurrentPages();
			//当前页面的数据
			var currPage = pages[pages.length - 1];  
			var data = currPage.$vm.data
			if(data != null){
				var needReflush = data.needReflush;
				if(needReflush == 1){
					this.listProjecttemp()
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
			listProjecttemp () {
				this.http.post('/openapi/task/projecttemp/list', {
					
				}).then((res) => {
					if(res.code != 0) return false
					this.projecttempList = res.rows;
				}) 
			},
			
			// 进入修改界面
			openUpdate(id){
				uni.navigateTo({
				    url: 'update/update?id='+id
				})
			},
			
			// 删除
			openDelete(id){
				var that = this
				this.vusui.confirm('您确定要删除当前记录吗？',{
					icon: 2
				}, function() {
					that.vusui.load(3)
					that.http.post('/openapi/task/projecttemp/delete', {
						ids: id
					}).then((res) => {
						that.vusui.close("loading")
						if(res.code != 0) {
							that.vusui.alert(res.msg)
							return false
						} else {
							that.vusui.alert('删除成功')
							that.listProjecttemp();
						}
					}) 
				})
			},
			
			// 步驟列表
			tempstep1(id){
				uni.navigateTo({
				    url: 'tempstep/tempstep?tempId='+id
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
