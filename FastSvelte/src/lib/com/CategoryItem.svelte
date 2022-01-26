<script>
	export let current;
	export let record;
	import Image from '$lib/com/Image.svelte';

	import MusicSVG from '$lib/icons/Music.svelte';
	import VideoSVG from '$lib/icons/Video.svelte';
	import WordSVG from '$lib/icons/Word.svelte';
	import ExcelSVG from '$lib/icons/Excel.svelte';
	import PptSVG from '$lib/icons/Ppt.svelte';
	import TxtSVG from '$lib/icons/Txt.svelte';
	import PdfSVG from '$lib/icons/Pdf.svelte';
	import ApkSVG from '$lib/icons/Apk.svelte';
	import ZipSVG from '$lib/icons/Zip.svelte';
	import UnknowSVG from '$lib/icons/Unknow.svelte';

	import Universal from '$lib/com/Universal.svelte';


	let icons=[null,MusicSVG,VideoSVG,WordSVG,ExcelSVG,PptSVG,TxtSVG,PdfSVG,ApkSVG,ZipSVG,UnknowSVG]

</script>

{#if 0 === current}
	<Image record={record}/>
{:else}
	<Universal Icon={icons[current]} record={record}/>
{/if}