<script>
	import { getContext } from 'svelte';
	import FaCloudDownloadAlt from 'svelte-icons/fa/FaCloudDownloadAlt.svelte'
	import FaExpand from 'svelte-icons/fa/FaExpand.svelte'
	import ImagePreview from '$lib/com/ImagePreview.svelte';

	export let record;

	let showMenu = false;
	let turnOn = () => {
		showMenu = true;
	};
	let turnOff = () => {
		showMenu = false;
	};

	const { open } = getContext('simple-modal');
	let preview = () => {
		open(ImagePreview, { record: record });
	};
</script>

<div class="container" on:mouseenter={turnOn} on:mouseleave={turnOff}>
	<img
		src="/images{record.path}?id={record.id}&width=128&height=128"
		alt={record.name}
		class={showMenu ? 'operater' : ''}
	/>
	{#if showMenu}
		<div class="items">
			<div class="item" on:click={preview}><FaExpand /></div>
			<a class="item" href="/downloads{record.path}" download={record.name}><FaCloudDownloadAlt /></a>
		</div>
	{/if}
</div>

<style>
	.container {
		width: 128px;
		height: 128px;
		margin: 16px;
		fill: var(--primary-color);
		position: relative;
	}
	img {
		object-fit: cover;
		width: 100%;
		height: 100%;
	}

	.operater {
		opacity: 0.3;
		background-color: var(--accent-color);
	}

	.items {
		position: absolute;
		display: flex;
		justify-content: center;
		align-items: center;
		left: 0;
		top: 0;
		right: 0;
		bottom: 0;
	}
	.item {
		margin: 8px;
		width: 32px;
		height: 32px;
		color: var(--primary-color);
	}
	.item:hover {
		cursor: pointer;
		color: var(--accent-color);
	}
</style>
