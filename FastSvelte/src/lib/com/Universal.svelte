<script>
	export let Icon;
	export let record;

	import File from '$lib/com/File.svelte';
</script>

<a href="/downloads{record.path}" download={record.name}>
	<svelte:component this={Icon} style="width: 24px;height: 24px;margin: 12px;" />
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
