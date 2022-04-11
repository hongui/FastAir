<script>
	export let data;
	export let record;

	import Icon from 'svelte-icon'
	import File from '$lib/com/File.svelte';
</script>

<a href="/downloads{record.path}" download={record.name}>
	<Icon data={data}/>
	<File {record} />
</a>

<style>
	a {
		display: flex;
		width: 100%;
		align-items: center;
		color: var(--primary-color);
	}
</style>
