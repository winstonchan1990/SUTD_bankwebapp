<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page session="true"%>

<!DOCTYPE html>
<html lang="en">
<%@include file="pageHeader.jsp"%>
<body>
	<%@include file="header.jsp"%>

	<main id="content" class="mainContent sutd-template" role="main">
	<div class="container">
		<%@include file="errorMessage.jsp"%>
		<div id="createTransaction">
			<form class="form-horizontal" id="newTransactionForm"
				action="newTransaction" enctype='multipart/form-data' method="post">
				<div id="input-group-txn-mode" class="form-group">
					<label for="txnType" >Mode:</label> 
					<select
						id="txnType" name="txnType" onchange="changeForm(this.value)">
						<option value="single">Single Transaction
						<option value="batch">Batch Transfer
					</select>
				</div>

				<div id="input-group-transcode" class="form-group">
					<label for="transcode" >Transaction
						code:</label> <input type="text" class="form-control" id="transcode"
						name="transcode" placeholder="Transaction Code">
				</div>


				<div id="input-group-toAccount" class="form-group">
					<label for="toAccountNum" class="control-label">To (account
						number)</label> <input type="number" class="form-control"
						id="toAccountNum" name="toAccountNum"
						placeholder="To Account Number">
				</div>
				<div id="input-group-amount" class="form-group">
					<label for="amount" class="control-label">Amount</label> <input
						type="number" class="form-control" id="amount" name="amount"
						placeholder="amount">
				</div>


				<div id="input-batch-file" class="form-group">
					<label for="batchFile" >Batch
						File:</label> <input type="file" id="fileSelect" name="fileSelect"
						disabled=true accept=".csv" />
				</div>

				<input id="token" name="token" type="hidden" value="${sessionScope.csrfToken}" />
				<button id="createTransBtn" type="submit" class="btn btn-default">Submit</button>
			</form>
		</div>

		<script>
			function changeForm(val) {
				if (val == "single") {
					var tc = document.getElementById("transcode");
					var ta = document.getElementById("toAccountNum");
					var am = document.getElementById("amount");
					var bf = document.getElementById("fileSelect");

					tc.disabled = false;
					tc.value = "";
					ta.disabled = false;
					ta.value = "";
					am.disabled = false;
					am.value = "";
					bf.disabled = true;
				} else {
					var tc = document.getElementById("transcode");
					var ta = document.getElementById("toAccountNum");
					var am = document.getElementById("amount");
					var bf = document.getElementById("fileSelect");

					tc.disabled = true;
					tc.value = "";
					ta.disabled = true;
					ta.value = "";
					am.disabled = true;
					am.value = "";
					bf.disabled = false;
				}
			}
		</script>
	</div>
	</main>
</body>
</html>
