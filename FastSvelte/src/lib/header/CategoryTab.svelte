<script>
	export let chooseTab;

	let categories = [
		'图片',
		'音乐',
		'视频',
		'Word文件',
		'Excel文件',
		'PowerPoint文件',
		'PDF文件',
		'文本文件',
		'安装包',
		'压缩包'
	];

	let current = 0;
	let scroller;
	let lastX = 0;
	let isMouseDown = false;

	let mouseDown = (event) => {
		isMouseDown = true;
		lastX = event.clientX;
	};
	let mouseUp = () => {
		isMouseDown = false;
	};
	let mouseMove = (event) => {
		if (isMouseDown) {
			let diff = lastX - event.clientX;
			lastX = event.clientX;
			scroller.scrollLeft += diff;
		}
	};
	let choose = (i) => {
		current = i;
		chooseTab(i);
	};
</script>

<svelte:head>
	<title>快传</title>
</svelte:head>

<section>
	<ul
		bind:this={scroller}
		on:mouseleave={mouseUp}
		on:mouseup={mouseUp}
		on:mousedown={mouseDown}
		on:mousemove={mouseMove}
	>
		{#each categories as cat, i}
			<li class={current === i ? 'current' : ''}>
				<span on:click={() => choose(i)}>{cat}</span>
			</li>
		{/each}
	</ul>
</section>

<style>
	ul {
		background-color: var(--primary-color);
		display: flex;
		align-items: center;
		overflow-x: auto;
		overflow-y: hidden;
		white-space: nowrap;
		height: 48px;
		width: 100%;
		padding: 0;
		margin: 0;
		color: white;
		scrollbar-width: none;
		-ms-overflow-style: none;
	}

	ul::-webkit-scrollbar {
		display: none;
	}

	ul:hover {
		cursor: grab;
		-webkit-touch-callout: none;
		-webkit-user-select: none;
		-moz-user-select: none;
		-ms-user-select: none;
		user-select: none;
	}

	li {
        height: 100%;
		list-style-type: none;
		text-align: center;
        position: relative;
	}

	li:hover {
		color: var(--accent-color);
		cursor: pointer;
	}

	span {
		display: inline-block;
        padding: 16px;
		width: 128px;
	}

	.current {
		color: var(--accent-color);
		font-weight: 500;
	}

	.current::after {
		content: '';
		border: 2px solid var(--accent-color);
		border-radius: 2px;
		height: 0px;
		font-size: 0px;
		position: absolute;
		left: 0;
		bottom: 0px;
        width: 100%;
		box-sizing: border-box;
	}
</style>
