<script>
	export let data;
	export let record;

	import File from '$lib/com/File.svelte';
</script>

<a href="/downloads{record.path}" download={record.name}>
	{data}
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
