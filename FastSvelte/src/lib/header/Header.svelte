<script>
	import { showUpload   } from '$lib/store.js';
	import FaFileUpload from 'svelte-icons/fa/FaFileUpload.svelte'
	import FaGithub from 'svelte-icons/fa/FaGithub.svelte'

	let show=true;
	showUpload.subscribe(value=>{
		show=value;
	})

</script>
<header>
	<div class="corner">
		<a href="/"><img src="favicon.png" alt="快传" /></a>
		<h3>体验飞起来的速度</h3>
		{#if show}
			<a href="/upload">
				<div>
					<FaFileUpload />
				</div>
			</a>
		{/if}
		<a href="https://github.com/hongui/FastAir">
			<div>
				<FaGithub />
			</div>
		</a>
	</div>
</header>

<style>
	header {
		background-color: var(--primary-color);
	}

	.corner {
		width: 100%;
		display: flex;
		justify-content: stretch;
		align-items: center;
	}

	.corner img {
		width: 2em;
		height: 2em;
		margin: 16px;
		object-fit: contain;
	}

	.corner h3 {
		width: 100%;
		color: white;
	}

	a div{
		color: white;
		width: 32px;
		height: 32px;
		cursor: pointer;
		margin: 10px;
	}

	a div:hover{
		color: var(--accent-color);;
	}
</style>
