<script>
	export let data;
	export let record;

	import File from '$lib/com/File.svelte';
</script>

<a href="/downloads{record.path}" download={record.name}>
	<div>
		<svelte:component this={data}/>
	</div>
	<File {record} />
</a>

<style>
	a {
		display: flex;
		width: 100%;
		align-items: center;
		color: var(--primary-color);
	}

	div{
		width: 32px;
		height: 32px;
		margin: 16px;
		color: var(--primary-color);
	}
</style>
