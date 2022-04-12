<script>
	export let current;
	export let record;
	import Image from '$lib/com/Image.svelte';

	import FaMusic from 'svelte-icons/fa/FaMusic.svelte'
	import FaFileVideo from 'svelte-icons/fa/FaFileVideo.svelte'
	import FaFileWord from 'svelte-icons/fa/FaFileWord.svelte'
	import FaFileExcel from 'svelte-icons/fa/FaFileExcel.svelte'
	import FaFilePowerpoint from 'svelte-icons/fa/FaFilePowerpoint.svelte'
	import FaFileAlt from 'svelte-icons/fa/FaFileAlt.svelte'
	import FaFilePdf from 'svelte-icons/fa/FaFilePdf.svelte'
	import FaAndroid from 'svelte-icons/fa/FaAndroid.svelte'
	import FaFileArchive from 'svelte-icons/fa/FaFileArchive.svelte'
	import FaFile from 'svelte-icons/fa/FaFile.svelte'

	import Universal from '$lib/com/Universal.svelte';


	let icons=[null,FaMusic,FaFileVideo,FaFileWord,FaFileExcel,FaFilePowerpoint,FaFileAlt,FaFilePdf,FaAndroid,FaFileArchive,FaFile]

</script>

{#if 0 === current}
	<Image record={record}/>
{:else}
	<Universal data={icons[current]} record={record}/>
{/if}