<template>
	<view>
		
		<vus-layer></vus-layer>
		
		<view class="cu-list menu">
			<view class="cu-item">
				<view class="content">登录名</view>
				<text >{{userInfo.loginName}}</text>
			</view>
			
			<view class="cu-item arrow"  @tap="toUpdateUserName(userInfo.userName)"  >
				<view class="content">姓名</view>
				<text >{{userInfo.userName}}</text>
			</view>
			
			<view class="cu-item arrow" @tap="toUpdatePhone(userInfo.phonenumber)"    >
				<view class="content">手机号</view>
				<view >{{userInfo.phonenumber}}</view>
			</view>
			
			<view class="cu-item arrow"  @tap="toUpdateEmail(userInfo.email)" >
				<view class="content">邮箱</view>
				<view >{{userInfo.email}}</view>
			</view>
			
			<view class="padding flex flex-direction" @tap="logout()">
				<button class="cu-btn bg-red margin-tb-sm lg">退出</button>
			</view>
			
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				userInfo: {
					
				}
				
			}
		},
		created () {
			this.initObject()
		},
		onShow() {
			var pages = getCurrentPages();
			//当前页面的数据
			var currPage = pages[pages.length - 1];  
			var data = currPage.$vm.data
			if(data != null){
				var needReflush = data.needReflush;
				if(needReflush == 1){
					this.initObject()
					var datax = {
						needReflush : 0
					}
					currPage.$vm.data=datax
				}
			} 
		},
		methods: {
			// 初始化个人信息
			initObject(){
				this.http.get('/openapi/system/userinfo').then((res) => {
					if(res.code != 0) return false
					this.userInfo = res.data
					this.userInfo.avatar = this.tool.formatURL(this.userInfo.avatar)
				})
			},
			
			// 修改姓名
			toUpdateUserName(userName){
				uni.navigateTo({
					url: 'updateUserName?userName='+userName
				})
			},
			
			// 进入修改手机页面
			toUpdatePhone(phonenumber) {
				uni.navigateTo({
					url: 'updatePhone?phonenumber='+phonenumber
				})
			},
			// 进入修改邮箱页面
			toUpdateEmail(email) {
				uni.navigateTo({
					url: 'updateEmail?email=' + email
				})
			},
			
			logout(){
				var that = this
				this.vusui.confirm('您确定要退出登陆吗？',{
					icon: 2
				}, function() {
					uni.removeStorageSync('token')
					uni.removeStorageSync('userId')
					uni.removeStorageSync('deptId')
					uni.removeStorageSync('deptName')
					uni.removeStorageSync('userName')
					uni.removeStorageSync('phonenumber')
					uni.removeStorageSync('avatar')
					
					uni.reLaunch({
						url: '../login/login'
					})
				})
			},
		}
	}
</script>

<style scoped>
	template{
		font-size:xx-large;
	}
</style>
