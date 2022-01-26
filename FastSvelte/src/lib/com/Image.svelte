<script>
	export let record;
</script>

<a href="/images{record.path}?id={record.id}" target="_blank">
	<img src="/images{record.path}?id={record.id}&width=128&height=128" alt={record.name} />
</a>

<style>
	img {
		margin: 16px;
		width: 128px;
		height: 128px;
		object-fit: cover;
	}
</style>