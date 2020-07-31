// JavaScript Document
var musics=[];
var music_pics=[];
var music_name=[];
$(function(){
		$.ajax({
			url:'/getMusicAll',
			type:'post',
			data:{},
			success:function(data){
				var content = "";
				$.each(data,function(index,item){
					musics.push(item.musicName);//音乐数组
					music_pics.push(item.musicPic);
					music_name.push(item.remark);//音乐中文名
					
					//渲染侧边栏
					//  var picUrl = "http://106.15.45.177:8080/pic/"+ item.musicPic;
                    var picUrl = "images/"+ item.musicPic;

					
					content +='<li class="item-content close-panel" onClick="goMusic('+item.id+')">'
						+'<div class="item-media">'
						+'<img src="'+picUrl+'" width="44">'
						+'</div>'
						+'<div class="item-inner">'
						+'<div class="item-title-row">'
						+'<div class="item-title">'+item.remark+'</div>'
						+' </div>'
						+'  <div class="item-subtitle">'+item.createTime+'</div>'
						+'  </div>'
						+' </li>';
				})	
				
				$(".list-block ul").html(content);
				
				//开始加载音乐
				getMusic();
			}
		
		})

				 setTimeout(function(){
                    duration.innerHTML=parseInt(music.duration/60)+":"+parseInt(music.duration%60);
				},5000);//一秒后读取音乐的总时长
})	

//选择单曲音乐

function goMusic(id){
	getMusic(id-1);
	music.play();//重载音乐后进行播放
}

	var music=document.getElementById("mymusic");
    var prograss=document.getElementById("prograss");
    var curtxt=document.getElementById("currenttime");
    var duration=document.getElementById("duration");
    var music_pic=document.getElementById("music_pic");
    var deg=0;//旋转角度
    var disctimer,prograsstimer;//碟片计时器,进度条计时器
    var musicindex=0;//音乐索引
//    musics=new Array("yuan.mp3","yuanyuan.mp3","feng.mp3");//音乐数组
    
//    music_pics=new Array("yuan","yuanY","feng");
    
    //旋转碟片
    var disc=document.getElementsByClassName('disc');
        
        //音乐时间显示
        function curtime(txt,misic)
        {
            if(music.currentTime<10)
                {
                    txt.innerHTML="0:0"+Math.floor(music.currentTime);
                }else
            if(music.currentTime<60)
                {
                    txt.innerHTML="0:"+Math.floor(music.currentTime);
                }
            else
                {
                    var minet=parseInt(music.currentTime/60);
                    var sec=music.currentTime-minet*60;
                    if(sec<10)
                        {
                            txt.innerHTML="0"+minet+":"+"0"+parseInt(sec);
                        }
                    else
                        {
                            txt.innerHTML="0"+minet+":"+parseInt(sec);
                        }
                }
        }
        
        //播放暂停
        function playPause()
        {
            var btn=document.getElementById("playbtn");
            if(music.paused)
                {
                    music.play();
                    clearInterval(disctimer);//清除碟片的定时器
                    btn.style.background="url(images/stop.png) no-repeat 10px";//改变播放暂停键的图标
                    disctimer=setInterval(function(){
                    disc[0].style.transform="rotate("+deg+"deg)";
                    deg+=0.5;
                        
                    //每秒设置进度条长度
                    },100);
                    prograsstimer=setInterval(function(){
                    prograss.style.width=(music.currentTime)*100 / (music.duration)+"%";
                    curtime(curtxt,music);
                    if(music.currentTime>=music.duration-1)//片尾跳转下一曲
                    {
                        musicindex++;//音乐索引加一
                        if(musicindex>=musics.length)//如果音乐索引超过长度，将音乐索引清零
                        {
                            musicindex=0;
                        }
                        getMusic();
                        music.play();//重载音乐后进行播放
                    }
                    },1000);
                }
            else
                {
                    music.pause();//停止音乐
                    btn.style.background="url(images/play.png) no-repeat 10px";
                    clearInterval(disctimer);//清除碟片滚动的定时器
                    clearInterval(prograsstimer);//清除进度条的定时器
                }
        }
    
    //下一曲
    function nextMusic()
    {
        musicindex++;//音乐索引加一
        if(musicindex>=musics.length)//如果音乐索引超过长度，将音乐索引清零
            {
                musicindex=0;
            }
        getMusic();
        music.play();
    }
    
    //上一曲
    function backMusic()
    {
        musicindex--;
        if(musicindex<0)//如果索引小于0，将索引变为最大值
            {
                musicindex=musics.length-1;
            }
        getMusic();
        music.play();
    }
        
    //读取音乐
        function getMusic(zz)
        {
 
        	if(zz != null){
				musicindex = zz;
			}
        	//Window
            /*	 console.log("====================进入win====================");
            	 music.src="images/music/"+musics[musicindex];//改变音乐的SRC
                 console.log("music.src:"+music.src);
                 music_pic.src="images/"+music_pics[musicindex];
                 console.log("music_pic.src:"+music_pic.src);
                 $("#spanVal").text(music_name[musicindex]);//音乐名称
*/                            
            	//linux
                 
        			console.log("====================进入Linux====================");
            console.log("没改之前music.src"+music.src);
            console.log("没改之前music_pic.src:"+music_pic.src);
            		music.src=":8080/music/"+musics[musicindex];//改变音乐的SRC
                    music_pic.src="images/"+music_pics[musicindex];
                    $("#spanVal").text(music_name[musicindex]);//音乐名称
      /*      music.src = music.src.replace("8181:","8080");
            music_pic.src = music_pic.src.replace("8181:","8080");*/
                 	music.src = music.src.replace("/:",":");
                 	music_pic.src = music_pic.src.replace("http://localhost:8080","");
                 	console.log("music.src:"+music.src);
                 	console.log("music_pic.src:"+music_pic.src);
             
           
           /* if(music.readyState="complete")
                {
                    setTimeout(function(){
                        duration.innerHTML=parseInt(music.duration/60)+":"+parseInt(music.duration%60);
                    },1000);//一秒后读取音乐的总时长
                    
               }*/
                 setTimeout(function(){
                     duration.innerHTML=parseInt(music.duration/60)+":"+parseInt(music.duration%60);
 				},5000);//一秒后读取音乐的总时长
        }
        
    
        window.onload=function(){//初始化页面
            //getMusic();
        	
        }