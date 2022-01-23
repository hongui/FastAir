<script>
	import CategoryTab from '$lib/header/CategoryTab.svelte'
	import Image from '$lib/com/Image.svelte'

	let loadCategory=async(i)=>{
		const response=await fetch("/category/"+i);
		let body=await response.json()
		console.log(body)
		return body.data
	}
	let current=0;
	let chooseTab=(i)=>{
		current=i;
		loadCategory(i);
	}
</script>

<svelte:head>
	<title>快传</title>
</svelte:head>

<section>
	<CategoryTab chooseTab={chooseTab}/>

	{#await loadCategory(current)}
		<p>load...</p>
	{:then data} 
	<div class="container">
		{#each data as image}
			<Image record={image}/>
		{/each}
	</div>
	{/await}
</section>

<style>
	.container{
		display: flex;
		flex-wrap: wrap;
		justify-content: space-evenly;
	}
</style>
