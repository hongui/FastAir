<script>
	import CategoryTab from '$lib/header/CategoryTab.svelte';
	import Image from '$lib/com/Image.svelte';
	import CategoryItem from '$lib/com/CategoryItem.svelte';

	let loadCategory = async (i) => {
		const response = await fetch('/category/' + i);
		let body = await response.json();
		return body.data;
	};

	let current = 0;
	let chooseTab = (i) => {
		current = i;
		loadCategory(i);
	};
</script>

<svelte:head>
	<title>快传</title>
</svelte:head>

<section>
	<CategoryTab {chooseTab} />

	{#await loadCategory(current)}
		<div class="full"><p>加载中...</p></div>
	{:then data}
		{#if 0 === data.length}
			<div class="full"><p>暂无文件~~</p></div>
		{:else}
			<div class="container">
				{#each data as record}
					<CategoryItem {current} {record} />
				{/each}
			</div>
		{/if}
	{:catch error}
		<div class="full">
			<p>{error}</p>
		</div>
	{/await}
</section>

<style>
	.container {
		display: flex;
		flex-wrap: wrap;
		justify-content: start;
	}

	.full {
		width: 100%;
		height: 100%;
		display: flex;
		justify-self: center;
		align-items: center;
	}
</style>
