# TableFixHeadersBasedOnCanvas
1.Rewrite open source TableFixHeaders project [TableFixHeaders](https://github.com/inQBarna/TableFixHeaders) as customized view
to improve the scolling performace in the case that more than
500 cells would be shown in one screen.
The optimization motivation is based on the fact that 
large scale Android ViewGroup
is not performance-friendly for rendering.
Current optimization is to reuse most part of the already rendered view data,
and only re-render the delta part due to scroll, the same as subview recycle.  

2.Support merged cell by introducing mergeId as cell's property  
3. ScreenShot
![image](https://github.com/pheecian/TableFixHeadersBasedOnCanvas/raw/master/screenshot.jpg)
