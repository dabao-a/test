<template>
	<view>
		<!-- 弹出层组件 -->
		<vus-layer></vus-layer>
		
		<!-- 搜索条件 -->
		<view >
			<view class="cu-list grid col-2" >
				<view class="cu-form-group solid-right">
					<view class="title">项目</view>
					<picker @change="pickerProject" :range="projectList" range-key="name">
						<view class="picker">{{project.name}}</view>
					</picker>
				</view>
				<view class="cu-form-group">
					<view class="title">状态</view>
					<picker @change="pickerStatus" :range="statusList" range-key="name">
						<view class="picker">{{statusName}}</view>
					</picker>
				</view>
			</view>
			
			<view class="cu-list grid col-2" >
				<view class="cu-form-group solid-right">
					<input placeholder="任务名称" v-model="searchName"></input>
				</view>
				<view class="margin-tb-sm text-center" @tap="searchTask()" >
					<button class="cu-btn bg-blue lg" >搜索</button>
				</view>
			</view>
		</view>
		
		<!-- 数据列表 -->
		<view class="cu-list menu" :class="['sm-border']">
			<view class="cu-item arrow"   :class="modalName=='move-box-'+ index?'move-cur':''"  
				 @tap="detail(pt.id)"   v-for="(pt,index) in taskList" :key="index"
				 @touchstart="ListTouchStart" @touchmove="ListTouchMove" @touchend="ListTouchEnd" :data-target="'move-box-' + index">
				<view class="content">
					<view >{{pt.name}}</view>
					<view class="text-gray">{{pt.yxj}}</view>
				</view>
				<view class="action">
					<text class="icon iconfont" v-if="pt.status == 0" >进行中</text>
					<text class="icon iconfont" v-if="pt.status == 1" >正常完成</text>
					<text class="icon iconfont" v-if="pt.status == 2" >超时完成</text>
					<text class="icon iconfont" v-if="pt.status == 3" >不能完成</text>
				</view>
				<view class="move">
					<view  v-if="pt.status == 0" class="bg-orange" @tap.stop="openUpdate(pt.id)" >修改</view>
					<view  v-if="pt.status == 0" class="bg-red"    @tap.stop="openDelete(pt.id)" >删除</view>
					<view  v-if="pt.status == 0" class="bg-blue"    @tap.stop="updateStatus(pt.id,4)" >完成</view>
					<view  v-if="pt.status == 0" class="bg-red"    @tap.stop="updateStatus(pt.id,3)" >不能</view>
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
				statusList: [
					{id:'',name:'全部状态'},
					{id:'0',name:'进行中'},
					{id:'1',name:'正常完成'},
					{id:'2',name:'超时完成'},
					{id:'3',name:'不能完成'}
				],
				statusName: '进行中',
				status: 0,
				taskList: [],
				project:{},
				searchName: '',
				projectList: []
			}
		},
		created () {
			this.listMytask()
		},
		onShow() {
			var pages = getCurrentPages();
			//当前页面的数据
			var currPage = pages[pages.length - 1];  
			var data = currPage.$vm.data
			if(data != null){
				var needReflush = data.needReflush;
				if(needReflush == 1){
					this.listMytask()
					var datax = {
						needReflush : 0
					}
					currPage.$vm.data=datax
				}
			} 
		},
		onLoad() {
			// 加载我参与的项目
			this.loadMyproject()
		},
		methods: {
			// 加载我参与的项目
			loadMyproject(){
				this.http.post('/openapi/task/project/user/listJoin', {
					
				}).then((res) => {
					if(res.code != 0) return false
					this.projectList = res.rows;
					this.project = this.projectList[0]
				}) 
			},
			// 选择项目
			pickerProject(e) {
				var index = e.detail.value
				this.project = this.projectList[index]
			},
			// 选择状态
			pickerStatus(e){
				var index = e.detail.value
				this.status = this.statusList[index].id
				this.statusName = this.statusList[index].name
			},
			
			// 搜索
			searchTask(){
				this.listMytask();
			},
			
			// 进入新增界面
			openAdd() {
				uni.navigateTo({ 
				    url:'../add/add'
				})
			},
			
			// 列表
			listMytask () {
				this.http.post('/openapi/task/task/mylist', {
					name: this.searchName,
					status: this.status,
					project: this.project
				}).then((res) => {
					if(res.code != 0) return false
					this.taskList = res.rows;
				}) 
			},
			
			// 进入修改界面
			openUpdate(id){
				uni.navigateTo({
				    url: '../update/update?id='+id
				})
			},
			
			// 进入详情
			detail(id){
				uni.navigateTo({
				    url: '../detail/detail?id='+id
				})
			},
			
			// 删除
			openDelete(id){
				var that = this
				this.vusui.confirm('您确定要删除当前记录吗？',{
					icon: 2
				}, function() {
					that.vusui.load(3)
					that.http.post('/openapi/task/task/delete', {
						ids: id
					}).then((res) => {
						that.vusui.close("loading")
						if(res.code != 0) {
							that.vusui.alert(res.msg)
							return false
						} else {
							that.vusui.alert('删除成功')
							that.listMytask();
						}
					}) 
				})
			},
			
			
			// 完成
			updateStatus(id,status){
				var that = this
				this.vusui.confirm('您确定要如此操作吗？',{
					icon: 2
				}, function() {
					that.vusui.load(3)
					that.http.post('/openapi/task/task/updateStatus', {
						id: id,
						status: status
					}).then((res) => {
						that.vusui.close("loading")
						if(res.code != 0) {
							that.vusui.alert(res.msg)
							return false
						} else {
							that.vusui.alert('操作成功')
							that.listMytask();
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
