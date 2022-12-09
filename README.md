<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=Android&logoColor=white"> <img src="https://img.shields.io/badge/JAVA-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/JSON-000000?style=for-the-badge&logo=json&logoColor=white">
</br></br>

<h1>간단한 영상 플레이어 앱</h1></br>



Android Studio - __Java__ only
<br/><br/>

_본 앱은 코딩연습을 위한 TestApp이므로 내포된 자료나 파일등은 관련성이 없으며, 실제 정보와 일치하지 않습니다._


----
</br></br>

<h2>1. 화면 구성</h2></br>


* 영상 리스트 화면(main)</br>

</t>![record3_AdobeExpress](https://user-images.githubusercontent.com/52855326/205234675-7b72b656-6d73-4405-8b27-1481aa4643c4.gif)


</br></br>
* 영상 재생화면(player)</br>

</t>![record4_AdobeExpress](https://user-images.githubusercontent.com/52855326/205235837-3c425d6e-6090-46e3-a914-e98cc15a2edb.gif)



</br></br>
<h2>2. 사용한 라이브러리</h2></br>

</br>
* ExoPlayer</br>

> implementation "com.google.android.exoplayer:exoplayer-core:2.18.1"

> implementation "com.google.android.exoplayer:exoplayer-ui:2.18.1"

> implementation "com.google.android.exoplayer:exoplayer-dash:2.18.1"


</br>
* Glide</br>

> implementation 'com.github.bumptech.glide:glide:4.11.0'

> annotationProcessor 'com.github.bumptech.glide:compiler:4.11.0'


</br>
* Vertical SeekBar</br>

> implementation 'com.h6ah4i.android.widget.verticalseekbar:verticalseekbar:1.0.0'


</br>
* ViewPager2</br>

> implementation 'androidx.viewpager2:viewpager2:1.0.0'
