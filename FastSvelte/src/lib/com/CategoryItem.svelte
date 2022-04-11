<script>
	export let current;
	export let record;
	import Image from '$lib/com/Image.svelte';

	import musicSVG from '$lib/icons/music.svg?raw';
	import videoSVG from '$lib/icons/video.svg?raw';
	import wordSVG from '$lib/icons/word.svg?raw';
	import excelSVG from '$lib/icons/excel.svg?raw';
	import pptSVG from '$lib/icons/ppt.svg?raw';
	import txtSVG from '$lib/icons/txt.svg?raw';
	import pdfSVG from '$lib/icons/pdf.svg?raw';
	import apkSVG from '$lib/icons/apk.svg?raw';
	import zipSVG from '$lib/icons/zip.svg?raw';
	import unknowSVG from '$lib/icons/unknow.svg?raw';

	import Universal from '$lib/com/Universal.svelte';


	let icons=[null,musicSVG,videoSVG,wordSVG,excelSVG,pptSVG,txtSVG,pdfSVG,apkSVG,zipSVG,unknowSVG]

</script>

{#if 0 === current}
	<Image record={record}/>
{:else}
	<Universal data={icons[current]} record={record}/>
{/if}