<script>
	let inputValue = '';
	let clips = [];
	let send = async () => {
		console.log(inputValue);
		let data = await fetch('/chatinfo', {
			method: 'put',
			body: inputValue
		});
		let json = data.json;
		clips = json.data;
		inputValue="";
	};
</script>

<div>
	<div>
		{#each clips as c}
			<p>{c}</p>
		{/each}
	</div>
	<div class="operator">
		<input bind:value={inputValue} />
		<button on:click={send}>发送</button>
	</div>
</div>

<style>
	.operator {
		position: fixed;
		bottom: 16px;
		left: 0;
		right: 0;
		display: flex;
	}

	input {
		flex: auto;
	}

	button {
		margin-left: 16px;
	}
</style>
