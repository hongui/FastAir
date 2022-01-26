<script>
	import Header from '$lib/header/Header.svelte';
	import '../app.css';
</script>

<Header />

<main>
	<slot />
</main>

<style>
	main {
		flex: 1;
		display: flex;
		flex-direction: column;
		width: 100%;
		height: 100vh;
		margin: 0 auto;
		box-sizing: border-box;
	}
</style>
