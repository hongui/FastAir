<script>
	import { showUpload   } from '$lib/store.js';
	import File from '$lib/com/File.svelte';
	import FaCloudDownloadAlt from 'svelte-icons/fa/FaCloudDownloadAlt.svelte'
	import FaTrashAlt from 'svelte-icons/fa/FaTrashAlt.svelte'

	showUpload.set(false);

	let dropFiles = [];
	let deny = (e) => {
		if (e.preventDefault) {
			e.preventDefault();
		} else {
			e.returnValue = false;
		}
		if (e.stopPropagation) {
			e.stopPropagation();
		} else {
			e.cancelBubble = true;
		}
		return false;
	};
	let droped = (e) => {
		let files = [];
		for (let f of e.dataTransfer.files) {
			f.date = f.lastModified;
			files.push(f);
		}
		dropFiles = dropFiles.concat(files);
		console.log(dropFiles);
		deny(e);
	};

	let reset = () => {
		dropFiles = [];
	};

	let post = () => {
		for (let f of dropFiles) {
			let bean = new FormData();
			bean.append('file', f, f.name);

			let request = new Request('/upload', {
				method: 'post',
				headers: {
					'Content-Type': 'multipart/formdata'
				},
				body: bean
			});
			fetch(request)
				.then((response) => {
					console.log(response);
				})
				.catch((reason) => {
					console.log(reason);
				});
		}
	};
	let operators = [
		{ icon: FaCloudDownloadAlt, action: post },
		{ icon: FaTrashAlt, action: reset }
	];
</script>

<div class="container">
	<div class="droparea active" on:dragenter={deny} on:dragover={deny} on:drop={droped}>
		<p>拖拽上传~</p>
	</div>
	<div class="files">
		{#each dropFiles as file}
			<File record={file} class="item" />
		{/each}
	</div>
</div>
<div class="operators">
	{#each operators as op}
		<div class="operator active activeIcon" on:click={op.action}>
			<svelte:component this={op.icon}/>
		</div>
	{/each}
</div>

<style>
	.container {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		height: 100%;
	}
	.droparea {
		border: 1px dashed var(--primary-color);
		border-radius: 2em;
		width: 90vw;
		height: 31.4vh;
		margin: 1em;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.droparea p {
		color: inherit;
		font-weight: 500;
	}

	.files {
		width: 100%;
		height: 100%;
	}

	.operators {
		position: fixed;
		right: 16px;
		bottom: 16px;
		display: flex;
		flex-direction: column;
	}

	.operator {
		width: 32px;
		height: 32px;
		margin-top: 16px;
	}

	.active{
		color: var(--primary-color);
		border-color: var(--primary-color);
	}

	.active:hover{
		color: var(--accent-color);
		border-color: var(--accent-color);
	}

	.activeIcon{

	}

	.activeIcon:hover{
		cursor: pointer;
	}
</style>
