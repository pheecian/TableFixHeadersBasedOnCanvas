# TableFixHeadersBasedOnCanvas
Rewrite open source TableFixHeaders project by customized view
to improve the scolling performace in the case that more than
500 cells would be shown in one screen.
The optimization is based on the fact that large scale Android ViewGroup
is not performance-friendly for rendering and scrolling.
Current optimization is to reuse most part of the already rendered view data,
and only re-render the delta part due to scroll.
