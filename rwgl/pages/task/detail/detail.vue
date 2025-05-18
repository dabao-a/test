<template>
	<view>

		<!-- 弹出层组件 -->
		<vus-layer></vus-layer>
		
		<uni-segmented-control :current="current" :values="items" @clickItem="onClickItem" style-type="text" :active-color="baseColor"></uni-segmented-control>
		
		<view class="content">
			<view v-show="current === 0">
				<form>
					<view class="cu-form-group">
						<view class="title">创  建   人</view>
						<input  v-model="task.user.userName"></input>
					</view>
					<view class="cu-form-group margin-top">
						<view class="title">任务名称</view>
						<input  v-model="task.name"></input>
					</view>
					<view class="cu-form-group margin-top">
						<view class="title">任务描述</view>
						<input  v-model="task.remark"></input>
					</view>
					<view class="cu-form-group margin-top">
						<view class="title">预计时间</view>
						<input  v-model="task.endTime"></input>
					</view>
					<view class="cu-form-group margin-top">
						<view class="title">实际时间</view>
						<input  v-model="task.finishTime"></input>
					</view>
					
					<view class="cu-form-group margin-top">
						<view class="title">所属项目</view>
						<input  v-model="task.project.name"></input>
					</view>
					
					<view class="cu-form-group margin-top">
						<view class="title">所属步骤</view>
						<input  v-model="task.step.name"></input>
					</view>
					
					<view class="cu-form-group margin-top">
						<view class="title">优  先   级</view>
						<input  v-model="task.yxj"></input>
					</view>
					<view class="margin-top"></view>
					<!-- 旧的附件列表  -->
					<view class="cu-form-group content"  v-for="(obj,index) in oldFileList" :key="index" >
						<view @tap="openFile(obj.path)"  >{{obj.name}}</view>
					</view>
				</form>
			</view>
				
			<view v-show="current === 1">
				<view class="cu-list menu" :class="['sm-border']">
					<view class="cu-item"    v-for="(msg,index) in messageList" :key="index">
						<view class="content">
							<view >{{msg.content}}</view>
							<view class="text-gray">{{msg.time}}</view>
						</view>
						<view class="action">
							<view >{{msg.user.userName}}</view>
						</view>
					</view>
				</view>
				
				<form>
					<view class="cu-form-group"></view>
					<view class="cu-form-group">
						<view class="title">评论内容</view>
						<input placeholder="请输入内容" v-model="message.content"></input>
					</view>
					<view class="grid col-1 padding-sm bg-white margin-top" @tap="add">
						<view class="margin-tb-sm text-center" >
							<button class="cu-btn bg-blue lg" >保存</button>
						</view>
					</view>
				</form>
				
			</view>
		</view>
		

	</view>
</template>

<script>
	import uniSegmentedControl from "@/components/uni-segmented-control/uni-segmented-control.vue"
	export default {
		data() {
			return {
				task: {
					user: {
						userId: '',
						userName: ''
					},
					project: {
						id: '',
						name: ''
					},
					label: {
						id: '',
						name: ''
					},
					step: {
						id: '',
						name: ''
					},
					yxj: '',
					flist: []
				},
				oldFileList: [],
				messageList: [],
				items: ['任务信息','评论列表'],
				current: 0,
				message: {
					content: '',
					task: {id:''}
				}
			}
		},
		created() {
			// 加载对象
			this.initObject()
			this.loadMessage()
		},
		onLoad(options) {
			this.task.id = options.id
			this.message.task.id = options.id
		},
		methods: {
			// 加载对象
			initObject(){
				this.http.get(`/openapi/task/task/get/${this.task.id}`).then((res) => {
					if(res.code != 0) {
						this.vusui.alert(res.msg)
						return false
					}
					this.task = res.data
					this.oldFileList = this.task.flist
				})
			},
			
			// 加载评论列表
			loadMessage(){
				this.http.get(`/openapi/task/task/message/list/${this.task.id}`).then((res) => {
					if(res.code != 0) {
						this.vusui.alert(res.msg)
						return false
					}
					this.messageList = res.rows
				})
			},
			
			// 打开文件
			openFile(path){
				var that = this
				uni.downloadFile({
				    url: this.baseURL + path, 
				    success: function (res) {
						var tempfile = res.tempFilePath
				        if (res.statusCode === 200) {
							uni.openDocument({
								filePath:tempfile
							})
				        } else {
							that.vusui.alert('文件打开失败')
						}
				    }
				});
			},
			
			// 切换
			onClickItem(e) {
				if (this.current !== e.currentIndex) {
					this.current = e.currentIndex;
				}
			},
			// 新增留言
			add() {
				var that = this
				this.vusui.load(3)
				this.http.post('/openapi/task/task/message/add', this.message).then((res) => {
					this.vusui.close("loading");
					if (res.code != 0) {
						this.vusui.alert(res.msg)
						return false
					} else {
						this.vusui.alert('新增成功', {
							yes: function() {
								that.loadMessage()
								that.message.content = ''
							}
						})
					}
				})
			}
		}
	}
</script>