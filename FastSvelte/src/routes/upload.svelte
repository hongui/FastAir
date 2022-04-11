<script>
	import Icon from 'svelte-icon'
	import File from '$lib/com/File.svelte';
	import cloudDownloadSVG from '$lib/icons/cloud_download.svg?raw';
	import deleteSVG from '$lib/icons/delete.svg?raw';

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
		{ icon: cloudDownloadSVG, action: post },
		{ icon: deleteSVG, action: reset }
	];
</script>

<div class="container">
	<div class="droparea" on:dragenter={deny} on:dragover={deny} on:drop={droped}>
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
		<div class="operator" on:click={op.action}>
			<Icon data={op.icon} size="36px" stroke="none"/>
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
		color: var(--primary-color);
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
		color: var(--primary-color);
		margin-top: 16px;
	}
	.operator:hover {
		color: var(--accent-color);
		cursor: pointer;
	}
</style>
