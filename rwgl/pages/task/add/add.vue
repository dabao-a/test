<template>
	<view>
		<!-- 弹出层组件 -->
		<vus-layer></vus-layer>
		<!-- 选择附件组件 -->
		<l-file ref="lFile" @up-success="upSuccess"></l-file>
		<form>
			<view class="cu-form-group">
				<view class="title">任务名称</view>
				<input placeholder="请输入任务名称" v-model="task.name"></input>
			</view>
			<view class="cu-form-group">
				<view class="title">任务描述</view>
				<input placeholder="请输入任务描述" v-model="task.remark"></input>
			</view>
			<view class="cu-form-group margin-top">
				<view class="title">完成时间</view>
				<picker mode="date" @change="dateChange">
					<view class="picker text-left">
						{{task.endTime}}
					</view>
				</picker>
			</view>
			<view class="cu-form-group">
				<view class="title">所属项目</view>
				<picker @change="pickerProject" :range="projectList" range-key="name">
					<view class="picker">
						{{task.project.name}}
					</view>
				</picker>
			</view>
			<view class="cu-form-group">
				<view class="title">所属步骤</view>
				<picker @change="pickerStep" :range="stepList" range-key="name">
					<view class="picker">
						{{task.step.name}}
					</view>
				</picker>
			</view>
			
			<view class="cu-form-group">
				<view class="title">所属标签</view>
				<picker @change="pickerLabel" :range="labelList" range-key="name">
					<view class="picker">
						{{task.label.name}}
					</view>
				</picker>
			</view>
			
			<view class="cu-form-group">
				<view class="title">优先级</view>
				<picker @change="pickerYxj" :range="yxjList" range-key="label">
					<view class="picker">
						{{task.yxj}}
					</view>
				</picker>
			</view>
			
			<view class="cu-form-group margin-top"   >
				<button class="cu-btn" @tap="uploadFile">附件上传</button>
			</view>
			<view class="cu-form-group content"  v-for="(obj,index) in task.flist" :key="index" >
				<!-- 长按根据下标删除 -->
				<view @longtap="deleteFileList(index)" >{{obj.name}}</view>
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
	import lFile from '@/components/l-file/l-file.vue'
	export default {
		components: {
			lFile
		},
		data() {
			return {
				task: {
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
					flist: [],
					endTime: this.dateutils.formatDate(new Date())
				},
				yxjList:[],
				labelList: [],
				stepList: [],
				projectList: []
			}
		},
		created() {
			// 加载项目
			this.loadMyproject()
			// 加载标签
			this.loadLabel()
			// 加载优先级
			this.loadYxj()
		},
		methods: {
			// 选择时间
			dateChange(e) {
				this.task.endTime = e.detail.value
			},
			// 加载项目
			loadMyproject(){
				this.http.post('/openapi/task/project/user/listJoin', {
					
				}).then((res) => {
					if(res.code != 0) return false
					this.projectList = res.rows;
					this.task.project = this.projectList[0]
					this.loadStep()
				}) 
			},
			// 选择项目
			pickerProject(e) {
				var index = e.detail.value
				this.task.project = this.projectList[index]
				this.loadStep()
			},
			// 加载步骤
			loadStep(){
				this.http.post(`/openapi/task/task/loadStepByProjectId/${this.task.project.id}`, {
					
				}).then((res) => {
					if(res.code != 0) return false
					this.stepList = res.rows;
					this.task.step = this.stepList[0]
				}) 
			},
			// 选择步骤
			pickerStep(e) {
				var index = e.detail.value
				this.task.step = this.stepList[index]
			},
			// 加载标签
			loadLabel(){
				this.http.post('/openapi/task/label/list', {
					
				}).then((res) => {
					if(res.code != 0) return false
					this.labelList = res.rows;
					this.task.label = this.labelList[0]
				}) 
			},
			// 选择标签
			pickerLabel(e) {
				var index = e.detail.value
				this.task.label = this.labelList[index]
			},
			// 加载优先级
			loadYxj(){
				this.http.post('/openapi/task/task/loadYxj', {
					
				}).then((res) => {
					if(res.code != 0) return false
					this.yxjList = res.rows;
					this.task.yxj = this.yxjList[0].value
				}) 
			},
			// 选择优先级
			pickerYxj(e) {
				var index = e.detail.value
				this.task.yxj = this.yxjList[index].value
			},
			
			// 上传文件
			uploadFile () {
				this.$refs.lFile.upload({
				    // #ifdef APP-PLUS
				    currentWebview: this.$mp.page.$getAppWebview(),
				    // #endif
					url: this.baseURL + '/openapi/task/task/uploadFile',
				    name: 'files'
				    // header: {'Authorization':uni.getStorageSync('token')},
				});
			},
			// 上传成功
			upSuccess (res) {
				var dataRes = JSON.parse(res.data.id)
				var path = dataRes.data[0].path;
				var name = dataRes.data[0].name;
				this.task.flist.push({
					path: path,
					name: name
				})
			},
			// 长按删除附件
			deleteFileList(e){
				var that = this
				this.vusui.confirm('您确定要删除当前附件吗？',{
					icon: 2
				}, function() {
					that.task.flist.splice(e,1)
				})
			},
			// 新增
			add() {
				this.vusui.load(3)
				this.http.post('/openapi/task/task/add', this.task).then((res) => {
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